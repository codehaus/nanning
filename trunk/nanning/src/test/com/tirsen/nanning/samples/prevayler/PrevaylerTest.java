package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.AspectFactory;
import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.attribute.AbstractAttributesTest;
import com.tirsen.nanning.config.AspectSystem;
import com.tirsen.nanning.config.MixinAspect;
import org.prevayler.Command;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;
import java.util.Iterator;

public class PrevaylerTest extends AbstractAttributesTest {

    private AspectFactory aspectFactory;

    private File prevaylerDir;

    protected void setUp() throws Exception {
        super.setUp();

        AspectSystem aspectSystem = new AspectSystem();
        aspectSystem.addAspect(new MixinAspect(MySystem.class, MySystemImpl.class));
        aspectSystem.addAspect(new MixinAspect(MyObject.class, MyObjectImpl.class));
        aspectSystem.addAspect(new PrevaylerAspect());

        aspectFactory = aspectSystem;
        Aspects.setContextAspectFactory(aspectFactory);

        prevaylerDir = File.createTempFile("test", "");
        prevaylerDir.delete();
        prevaylerDir.mkdirs();
        prevaylerDir.deleteOnExit();
    }

    protected void tearDown() throws Exception {
        prevaylerDir.delete();
    }

    public void test() throws IOException, ClassNotFoundException {
        final CountingPrevayler prevayler = newPrevayler();
        CurrentPrevayler.withPrevayler(prevayler, new Runnable() {
            public void run() {
                MyObject insideObject = currentMySystem().createMyObject();
                prevayler.assertNumberOfCommands("create should result in one command only", 1);
                currentMySystem().setMyObject(insideObject);
                prevayler.assertNumberOfCommands("set should result in one command only", 2);
                assertTrue("should have gotten an object ID", currentMySystem().hasObjectID(insideObject));

                insideObject.setAttribute("oldValue");
                prevayler.assertNumberOfCommands(3);
                insideObject.setAttribute("newValue");
                prevayler.assertNumberOfCommands(4);

                MyObject outsideObject = (MyObject) aspectFactory.newInstance(MyObject.class);
                prevayler.assertNumberOfCommands("no command when object created outside prevayler", 4);
                assertFalse("object created outside Prevayler should not get an object ID",
                        currentMySystem().hasObjectID(outsideObject));

                MyObject outsideNestedObject = (MyObject) aspectFactory.newInstance(MyObject.class);
                prevayler.assertNumberOfCommands("no command when object created outside prevayler", 4);
                assertFalse("object created outside Prevayler should not get an object ID",
                        currentMySystem().hasObjectID(outsideNestedObject));
                outsideObject.setMyObject(outsideNestedObject);
                prevayler.assertNumberOfCommands("no command when operating on object outside prevayler", 4);
                assertFalse("object created outside Prevayler " +
                        "and used as argument to another object created ouside Prevayler should not get an object ID",
                        currentMySystem().hasObjectID(outsideNestedObject));

                // mixing prevayler and non-prevayler stuff the other way around will mess things up
                // uncomment this line and watch the nice little assert failure
                // there's another line in checkMySystem() that explains the real problem
                //outsideNestedObject.setMyObject(insideObject);

                insideObject.setMyObject(outsideObject);
                prevayler.assertNumberOfCommands(
                        "command when operating on object inside prevayler with object outside prevayler", 5);
                assertTrue("when object is put inside Prevayler it should get an object ID",
                        currentMySystem().hasObjectID(outsideObject));
                assertTrue("even nested objects put inside Prevayler should get object IDs (this is tricky stuff!)",
                        currentMySystem().hasObjectID(outsideNestedObject));

                Collection  objects = currentMySystem().getAllRegisteredObjects();
                assertEquals("objects not created ", 4, objects.size());
                Iterator iterator = objects.iterator(); iterator.next();
                insideObject = (MyObject) iterator.next();
                assertEquals("attribute not correct value", "newValue", insideObject.getAttribute());
                assertNotNull(insideObject.getMyObject());
            }
        });

        // reload database
        checkMySystem();

        // reload database with snapshot
        prevayler.takeSnapshot();
        checkMySystem();
    }

    private void checkMySystem() throws IOException, ClassNotFoundException {
        final CountingPrevayler prevayler = newPrevayler();
        CurrentPrevayler.withPrevayler(prevayler, new Runnable() {
            public void run() {
                Collection objects = currentMySystem().getAllRegisteredObjects();
                assertEquals("objects not persisted", 4, objects.size());
                Iterator iterator = objects.iterator(); iterator.next();
                MyObject myObject = (MyObject) iterator.next();
                assertEquals("property not correct value", "newValue", myObject.getAttribute());
                prevayler.assertNumberOfCommands("just checking, should be no commands", 0);

                // this will not hold if you mix objects created inside prevayler with those created outside
                // this object will not be identified correctly in the command and you will have two copies of the
                // "same" object
                //assertSame(myObject, myObject.getMyObject().getMyObject().getMyObject());
            }
        });
    }

    public void testGarbageCollect() throws IOException, ClassNotFoundException {
        CountingPrevayler prevayler = newPrevayler();
        CurrentPrevayler.withPrevayler(prevayler, new Runnable() {
            public void run() {
                MyObject myObject = (MyObject) aspectFactory.newInstance(MyObject.class);
                currentMySystem().setMyObject(myObject);
                myObject.setMyObject((MyObject) aspectFactory.newInstance(MyObject.class));
                assertEquals("three objects should have been created", 3, currentMySystem().getAllRegisteredObjects().size());
            }
        });

        // restoring
        prevayler.takeSnapshot();
        prevayler = newPrevayler();
        CurrentPrevayler.withPrevayler(prevayler, new Runnable() {
            public void run() {
                assertEquals("three objects should be left", 3, currentMySystem().getAllRegisteredObjects().size());
                // removing one of the objects
                assertNotNull(currentMySystem().getMyObject().getMyObject());
                currentMySystem().getMyObject().setMyObject(null);
                assertEquals("garbage collect on snapshot only, three object should be here still",
                        3, currentMySystem().getAllRegisteredObjects().size());
            }
        });

        // restoring and garbage collecting
        prevayler.takeSnapshot();
        prevayler = newPrevayler();
        CurrentPrevayler.withPrevayler(prevayler, new Runnable() {
            public void run() {
                assertNull(currentMySystem().getMyObject().getMyObject());
                assertEquals("two objects should be left, one garbage collected", 2, currentMySystem().getAllRegisteredObjects().size());
            }
        });

    }

    public void testFinalizationCallback() throws Exception {
        CurrentPrevayler.withPrevayler(newPrevayler(), new PrevaylerAction() {
            public Object run() throws Exception {
                MySystem mySystem = currentMySystem();
                MyObject myObject = (MyObject) aspectFactory.newInstance(MyObject.class);
                mySystem.setMyObject(myObject);
                GarbageCollectingPrevayler.garbageCollectSystem(mySystem);
                assertEquals("gc didn't work", 2, mySystem.getAllRegisteredObjects().size());
                assertFalse("finalization called too early", myObject.wasFinalized());
                mySystem.setMyObject(null);
                GarbageCollectingPrevayler.garbageCollectSystem(mySystem);
                assertEquals("gc didn't work", 1, mySystem.getAllRegisteredObjects().size());
                assertTrue("finalization not called", myObject.wasFinalized());

                return null;
            }
        });
    }

    private static MySystem currentMySystem() {
        return (MySystem) CurrentPrevayler.getSystem();
    }

    private static class CountingPrevayler extends GarbageCollectingPrevayler {
        private int numberOfCommandsInLog = 0;

        public CountingPrevayler(IdentifyingSystem system, String dir) throws IOException, ClassNotFoundException {
            super(system, dir);
        }

        public Serializable executeCommand(Command command) throws Exception {
            numberOfCommandsInLog++;
            return super.executeCommand(command);
        }

        public void takeSnapshot() throws IOException {
            numberOfCommandsInLog = 0;
            super.takeSnapshot();
        }

        public void assertNumberOfCommands(String message, int expectedNumber) {
            assertEquals(message + ", wrong number of commands in log", expectedNumber, numberOfCommandsInLog);
        }

        public void assertNumberOfCommands(int expectedNumber) {
            assertEquals("wrong number of commands in log", expectedNumber, numberOfCommandsInLog);
        }
    }

    private CountingPrevayler newPrevayler() throws IOException, ClassNotFoundException {
        CountingPrevayler prevayler = new CountingPrevayler((IdentifyingSystem) aspectFactory.newInstance(MySystem.class),
                                prevaylerDir.getAbsolutePath());
        return prevayler;
    }
}
