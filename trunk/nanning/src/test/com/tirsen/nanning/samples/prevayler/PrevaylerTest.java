package com.tirsen.nanning.samples.prevayler;

import java.io.*;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.IdentityHashMap;

import com.tirsen.nanning.AspectFactory;
import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.MixinInstance;
import com.tirsen.nanning.attribute.AbstractAttributesTest;
import com.tirsen.nanning.attribute.Attributes;
import com.tirsen.nanning.config.AspectSystem;
import com.tirsen.nanning.config.FindTargetMixinAspect;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.prevayler.implementation.CheckpointPrevayler;

public class PrevaylerTest extends AbstractAttributesTest {

    private AspectFactory aspectFactory;

    private File prevaylerDir;

    protected void setUp() throws Exception {
        super.setUp();

        AspectSystem aspectSystem = new AspectSystem();
        aspectSystem.addAspect(new FindTargetMixinAspect());
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
                prevayler.assertNumberOfCommands("create should not result in a command", 0);
                currentMySystem().setMyObject(insideObject);
                prevayler.assertNumberOfCommands("set should result in one command only", 1);

                insideObject.setValue("oldValue");
                prevayler.assertNumberOfCommands(2);
                insideObject.setValue("newValue");
                prevayler.assertNumberOfCommands(3);

                MyObject outsideObject = (MyObject) aspectFactory.newInstance(MyObject.class);
                prevayler.assertNumberOfCommands("no command when object created outside prevayler", 3);

                MyObject outsideNestedObject = (MyObject) aspectFactory.newInstance(MyObject.class);
                prevayler.assertNumberOfCommands("no command when object created outside prevayler", 3);

                outsideObject.setMyObject(outsideNestedObject);
                prevayler.assertNumberOfCommands("commands operating on objects outside prevayler still generates " +
                                                 "commands (they shouldn't really but we haven't implemented that yet)",
                                                 4);

                // mixing prevayler and non-prevayler stuff the other way around will mess things up
                // uncomment this line and watch the nice little assert failure
                // there's another line in checkMySystem() that explains the real problem
                //                outsideNestedObject.setMyObject(insideObject);

                insideObject.setMyObject(outsideObject);
                prevayler.assertNumberOfCommands(
                        "command when operating on object inside prevayler with object outside prevayler", 5);

                Collection objects = currentMySystem().getAllObjects();
                assertEquals("objects not created ", 3, objects.size());
                final MyObject objectToFind = insideObject;
                insideObject = (MyObject) CollectionUtils.find(objects, new Predicate() {
                    public boolean evaluate(Object o) {
                        return o == objectToFind;
                    }
                });
                assertNotNull(insideObject);
                assertEquals("value not correct", "newValue", insideObject.getValue());
                assertNotNull(insideObject.getMyObject());
            }
        });

        // reload database
        checkMySystem();

        // reload database with snapshot
        ((CheckpointPrevayler) prevayler.getWrappedPrevayler()).checkpoint();
        checkMySystem();
    }

    private void checkMySystem() throws IOException, ClassNotFoundException {
        final CountingPrevayler prevayler = newPrevayler();
        CurrentPrevayler.withPrevayler(prevayler, new Runnable() {
            public void run() {
                Collection objects = currentMySystem().getAllObjects();
                assertEquals("objects not persisted", 3, objects.size());
                MyObject myObject = currentMySystem().getMyObject();
                assertEquals("property not correct value", "newValue", myObject.getValue());
                prevayler.assertNumberOfCommands("just checking, should be no commands", 0);

                // this will not hold if you mix objects created inside prevayler with those created outside
                // this object will not be identified correctly in the command and you will have two copies of the
                // "same" object
                //                assertSame(myObject, myObject.getMyObject().getMyObject().getMyObject());
            }
        });
    }

    public void testABC() throws ClassNotFoundException, IOException, NoSuchMethodException {
        AspectInstance aspectInstance = Aspects.getAspectInstance(aspectFactory.newInstance(MyObject.class));
        MixinInstance mixinInstance = (MixinInstance) aspectInstance.getMixins().iterator().next();

        Method setValue = MyObject.class.getDeclaredMethod("setValue", new Class[]{String.class});
        assertEquals(2, mixinInstance.getInterceptorsForMethod(setValue).size());

        Method setABC = MyObject.class.getDeclaredMethod("setABC", new Class[]{String[].class});
        assertTrue(Attributes.getAttributes(MyObject.class).hasAttribute(setABC, "transaction"));
        assertTrue(Attributes.hasAttribute(setABC, "transaction"));
        assertEquals(2, mixinInstance.getInterceptorsForMethod(setABC).size());

        final CountingPrevayler prevayler = newPrevayler();
        CurrentPrevayler.withPrevayler(prevayler, new Runnable() {
            public void run() {
                MyObject myObject = currentMySystem().createMyObject();
                prevayler.assertNumberOfCommands(0);
                myObject.setABC(null);
                prevayler.assertNumberOfCommands(1);
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
                assertEquals("two MyObjects should have been created", 2, currentMySystem().getAllObjects().size());
            }
        });

        // restoring
        ((CheckpointPrevayler) prevayler.getWrappedPrevayler()).checkpoint();
        prevayler = newPrevayler();
        CurrentPrevayler.withPrevayler(prevayler, new Runnable() {
            public void run() {
                assertEquals("two MyObjects should be left", 2, currentMySystem().getAllObjects().size());
                // removing one of the objects
                assertNotNull(currentMySystem().getMyObject().getMyObject());
                currentMySystem().getMyObject().setMyObject(null);
            }
        });

        // restoring and garbage collecting
        ((CheckpointPrevayler) prevayler.getWrappedPrevayler()).checkpoint();

        prevayler = newPrevayler();
        CurrentPrevayler.withPrevayler(prevayler, new Runnable() {
            public void run() {
                assertNull(currentMySystem().getMyObject().getMyObject());
                assertEquals("1 MyObjects should be left, one garbage collected", 1, currentMySystem().getAllObjects().size());
            }
        });
    }

    public void testSerialization() throws IOException, ClassNotFoundException {
        MyObject myObject = (MyObject) aspectFactory.newInstance(MyObject.class);
        myObject.setValue("value");
        IdentityHashMap identityHashMap = new IdentityHashMap();
        identityHashMap.put(myObject, new Long(1));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(identityHashMap);
        objectOutputStream.close();
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
        identityHashMap = (IdentityHashMap) objectInputStream.readObject();

        myObject = (MyObject) identityHashMap.keySet().iterator().next();
        assertEquals("value", myObject.getValue());
    }

//    public void testOptionalDataException() throws IOException, ClassNotFoundException {
//        MySystem mySystem = (MySystem) aspectFactory.newInstance(MySystem.class);
//        mySystem.registerObjectID(aspectFactory.newInstance(MyObject.class));
//        assertEquals(1, mySystem.getObjectID(mySystem.getAllRegisteredObjects().iterator().next()));
//        mySystem = (MySystem) serialize(mySystem);
//        mySystem = (MySystem) serialize(mySystem);
//        assertEquals(1, mySystem.getObjectID(mySystem.getAllRegisteredObjects().iterator().next()));
//        assertEquals(2, mySystem.getAllRegisteredObjects().size());
//    }
//
//    private Object serialize(Object o) throws IOException, ClassNotFoundException {
//        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//        ObjectOutputStream objectOutputStream = new ObjectOutputStream(buffer);
//        objectOutputStream.writeObject(o);
//        return new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray())).readObject();
//    }

    private static MySystem currentMySystem() {
        return (MySystem) CurrentPrevayler.getSystem();
    }

    private CountingPrevayler newPrevayler() throws IOException, ClassNotFoundException {
        CountingPrevayler prevayler = new CountingPrevayler(new CheckpointPrevayler(aspectFactory.newInstance(MySystem.class),
                                                                                    prevaylerDir.getAbsolutePath()));
        return prevayler;
    }

    public void testUnsupportedTransaction() {
        assertTrue(CheckTransactionUnsupportedInterceptor.isTransactionsSupported());
        TestUnsupportedTransaction testUnsupportedTransaction =
                (TestUnsupportedTransaction) aspectFactory.newInstance(TestUnsupportedTransaction.class);
        try {
            testUnsupportedTransaction.callWithUnsupportedTransaction();
            fail();
        } catch (IllegalStateException shouldHappen) {
        }
        assertTrue(CheckTransactionUnsupportedInterceptor.isTransactionsSupported());
    }

    public void testIsTransactional() throws NoSuchMethodException {
        assertFalse(PrevaylerInterceptor.isTransactional(null));
        assertFalse(PrevaylerInterceptor.isTransactional(Object.class));
        assertTrue(PrevaylerInterceptor.isTransactional(MySystem.class));
        assertFalse(PrevaylerInterceptor.transactionalReturnValue(
                MySystem.class.getMethod("setMyObject", new Class[]{MyObject.class})));
        assertTrue(PrevaylerInterceptor.transactionalReturnValue(MySystem.class.getMethod("createMyObject", null)));
    }
}
