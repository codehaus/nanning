package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.attribute.AbstractAttributesTest;
import com.tirsen.nanning.definition.AspectClass;
import com.tirsen.nanning.definition.AspectRepository;
import com.tirsen.nanning.definition.InterceptorDefinition;
import org.prevayler.PrevalentSystem;
import org.prevayler.Prevayler;
import org.prevayler.Command;
import org.prevayler.implementation.SnapshotPrevayler;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class PrevaylerTest extends AbstractAttributesTest {

    private AspectRepository aspectRepository;

    private File prevaylerDir;

    protected void setUp() throws Exception {
        super.setUp();

        aspectRepository = new AspectRepository();

        aspectRepository.defineInterceptor(new InterceptorDefinition(PrevaylerInterceptor.class));

        {
            AspectClass aspectClass = new AspectClass();
            aspectClass.setInterface(MyObject.class);
            aspectClass.addInterceptor(aspectRepository.getInterceptor(PrevaylerInterceptor.class));
            aspectClass.setTarget(MyObjectImpl.class);
            aspectRepository.defineClass(aspectClass);
        }

        {
            AspectClass aspectClass = new AspectClass();
            aspectClass.setInterface(MySystem.class);
            aspectClass.addInterceptor(aspectRepository.getInterceptor(PrevaylerInterceptor.class));
            aspectClass.setTarget(MySystemImpl.class);
            aspectRepository.defineClass(aspectClass);
        }

        Aspects.setContextAspectFactory(aspectRepository);

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

                MyObject outsideObject = (MyObject) aspectRepository.newInstance(MyObject.class);
                prevayler.assertNumberOfCommands("no command when object created outside prevayler", 4);
                assertFalse("object created outside Prevayler should not get an object ID",
                        currentMySystem().hasObjectID(outsideObject));

                MyObject outsideNestedObject = (MyObject) aspectRepository.newInstance(MyObject.class);
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

                List objects = currentMySystem().getObjects();
                assertEquals("objects not created ", 4, objects.size());
                insideObject = (MyObject) objects.get(1);
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
                List objects = currentMySystem().getObjects();
                assertEquals("objects not persisted", 4, objects.size());
                MyObject myObject = (MyObject) objects.get(1);
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
                MyObject myObject = (MyObject) aspectRepository.newInstance(MyObject.class);
                currentMySystem().setMyObject(myObject);
                myObject.setMyObject((MyObject) aspectRepository.newInstance(MyObject.class));
                assertEquals("three objects should have been created", 3, currentMySystem().getObjects().size());
            }
        });

        // restoring
        prevayler.takeSnapshot();
        prevayler = newPrevayler();
        CurrentPrevayler.withPrevayler(prevayler, new Runnable() {
            public void run() {
                assertEquals("three objects should be left", 3, currentMySystem().getObjects().size());
                // removing one of the objects
                assertNotNull(currentMySystem().getMyObject().getMyObject());
                currentMySystem().getMyObject().setMyObject(null);
                assertEquals("garbage collect on snapshot only, three object should be here still",
                        3, currentMySystem().getObjects().size());
            }
        });

        // restoring and garbage collecting
        prevayler.takeSnapshot();
        prevayler = newPrevayler();
        CurrentPrevayler.withPrevayler(prevayler, new Runnable() {
            public void run() {
                assertEquals("two objects should be left, one garbage collected", 2, currentMySystem().getObjects().size());
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
        CountingPrevayler prevayler = new CountingPrevayler((IdentifyingSystem) aspectRepository.newInstance(MySystem.class),
                                prevaylerDir.getAbsolutePath());
        return prevayler;
    }
}
