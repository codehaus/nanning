package org.codehaus.nanning.prevayler;

import java.io.*;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.IdentityHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.codehaus.nanning.AspectFactory;
import org.codehaus.nanning.AspectInstance;
import org.codehaus.nanning.Aspects;
import org.codehaus.nanning.Mixin;
import org.codehaus.nanning.attribute.AbstractAttributesTest;
import org.codehaus.nanning.attribute.Attributes;
import org.codehaus.nanning.config.AspectSystem;
import org.codehaus.nanning.config.FindTargetMixinAspect;
import org.prevayler.PrevaylerFactory;

public class PrevaylerTest extends AbstractAttributesTest {

    private AspectFactory aspectFactory;

    private File prevaylerDir;

    protected void setUp() throws Exception {
        super.setUp();

        assertTrue("attributes not compiled or not on classpath (add 'target/attributes' to classpath)",
                PrevaylerUtils.isTransactional(MySystem.class.getMethod("setMyObject", new Class[]{MyObject.class})));
        assertTrue("attributes not compiled or not on classpath (add 'target/attributes' to classpath)",
                PrevaylerUtils.isTransactional(MySystem.class.getMethod("setSimpleString", new Class[]{String.class})));

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

    public void testMethodCallWithSimpleStringIsPersisted() throws Exception {
        final CountingPrevayler prevayler = newPrevayler();
        CurrentPrevayler.withPrevayler(prevayler, new PrevaylerAction() {
            public Object run() throws Exception {
                assertFalse(currentMySystem().hasObjectID(currentMySystem()));
                currentMySystem().setSimpleString("string");
                InvokeCommand invokeCommand = (InvokeCommand) prevayler.getTransaction(0);
                IdentifyingCall call = (IdentifyingCall) invokeCommand.getCall();
                assertTrue(call.marshal(currentMySystem()) instanceof Identity);
                assertTrue(call.target instanceof Identity);
                assertEquals("string", call.args[0]);

                prevayler.assertTransactionLog("setSimpleString");
                assertEquals("string", currentMySystem().getSimpleString());
                return null;
            }
        });
        final CountingPrevayler loadedPrevayler = newPrevayler();
        CurrentPrevayler.withPrevayler(loadedPrevayler, new PrevaylerAction() {
            public Object run() throws Exception {
                assertEquals("string", currentMySystem().getSimpleString());
                return null;
            }
        });
    }



    /**
     * Big badass functional test. I hate it, I want to kill it...
     */
    public void test() throws Exception {
        final CountingPrevayler prevayler = newPrevayler();
        CurrentPrevayler.withPrevayler(prevayler, new PrevaylerAction() {
            public Object run() throws NoSuchMethodException {
                MyObject insideObject = currentMySystem().createMyObject();
                prevayler.assertNumberOfCommands("create should not result in a command", 0);
                currentMySystem().setMyObject(insideObject);
                prevayler.assertTransactionLog("setMyObject");

                assertTrue("Prevayler is lost after first transaction", CurrentPrevayler.hasPrevayler());
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

                return null;
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
        Mixin mixinInstance = (Mixin) aspectInstance.getMixins().iterator().next();

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
        prevayler.takeSnapshot();
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
        prevayler.takeSnapshot();
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
        CountingPrevayler prevayler = new CountingPrevayler(
                PrevaylerFactory.createPrevayler(aspectFactory.newInstance(MySystem.class),
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
