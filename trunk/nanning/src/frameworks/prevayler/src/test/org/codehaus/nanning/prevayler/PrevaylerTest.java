package org.codehaus.nanning.prevayler;

import java.io.*;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.codehaus.nanning.AspectInstance;
import org.codehaus.nanning.Aspects;
import org.codehaus.nanning.Mixin;
import org.codehaus.nanning.attribute.AbstractAttributesTest;
import org.codehaus.nanning.attribute.Attributes;
import org.codehaus.nanning.config.AspectSystem;
import org.codehaus.nanning.config.FindTargetMixinAspect;
import org.prevayler.PrevaylerFactory;
import junit.framework.Assert;

public class PrevaylerTest extends AbstractAttributesTest {

    private File prevaylerDir;
    private CountingPrevayler currentPrevayler;
    private AspectSystem aspectSystem;

    protected void setUp() throws Exception {
        super.setUp();

        assertTrue("attributes not compiled or not on classpath (add 'target/attributes' to classpath)",
                   PrevaylerUtils.isTransactional(MySystem.class.getMethod("setMyObject", new Class[]{MyObject.class})));
        assertTrue("attributes not compiled or not on classpath (add 'target/attributes' to classpath)",
                   PrevaylerUtils.isTransactional(MySystem.class.getMethod("setSimpleString", new Class[]{String.class})));

        aspectSystem = new AspectSystem();
        aspectSystem.addAspect(new FindTargetMixinAspect());
        aspectSystem.addAspect(new PrevaylerAspect());

        Aspects.setContextAspectFactory(aspectSystem);

        prevaylerDir = File.createTempFile("test", "");
        prevaylerDir.delete();
        prevaylerDir.mkdirs();
        prevaylerDir.deleteOnExit();
    }

    protected void tearDown() throws Exception {
        prevaylerDir.delete();
    }

    public void testMethodCallWithSimpleStringIsPersisted() throws Exception {
        newPrevayler();
        withCurrentPrevayler(new PrevaylerAction() {
            public Object run() throws Exception {
                assertFalse(((Identifiable) currentSystem()).hasObjectID());
                currentSystem().setSimpleString("string");
                currentPrevayler.assertTransactionLog("setSimpleString");
                assertEquals("string", currentSystem().getSimpleString());
                return null;
            }
        });
        newPrevayler();
        withCurrentPrevayler(new PrevaylerAction() {
            public Object run() throws Exception {
                assertEquals("string", currentSystem().getSimpleString());
                return null;
            }
        });
    }

    public void testMethodCallWithCircularGraphOfEntitiesIsPersisted() throws Exception {
        newPrevayler();
        final MyObject a = (MyObject) newInstance(MyObject.class);
        final MyObject b = (MyObject) newInstance(MyObject.class);
        withCurrentPrevayler(new PrevaylerAction() {
            public Object run() throws Exception {
                a.setMyObject(b);
                b.setMyObject(a);
                currentSystem().add(a);
                currentSystem().add(b);

                return null;
            }
        });

        final long aId = getObjectId(a);
        final long bId = getObjectId(b);

        newPrevayler();
        withCurrentPrevayler(new PrevaylerAction() {
            public Object run() throws Exception {
                MyObject loadedA = (MyObject) currentSystem().getIdentifiable(aId);
                MyObject loadedB = (MyObject) currentSystem().getIdentifiable(bId);
                assertSame(loadedB, loadedA.getMyObject());
                assertSame(loadedA, loadedB.getMyObject());

                return null;
            }
        });
    }

    long getObjectId(Object o) {
        return ((Identifiable) o).getObjectID();
    }

    public static class EntityReference implements Serializable {
        MyObject object;

        public EntityReference(MyObject object) {
            this.object = object;
        }
    }

    public void testMethodCallWithValueObjectReferencingEntityIsPersisted() throws Exception {
        newPrevayler();
        final MyObject object = (MyObject) newInstance(MyObject.class);

        withCurrentPrevayler(new PrevaylerAction() {
            public Object run() throws Exception {
                EntityReference ref = new EntityReference(object);
                assertSame(object, ref.object);
                currentSystem().setSimpleObject(ref);
                assertTrue(((Identifiable) object).hasObjectID());

                return null;
            }
        });

        final long objectId = getObjectId(object);

        newPrevayler();
        CurrentPrevayler.withPrevayler(currentPrevayler, new PrevaylerAction() {
            public Object run() throws Exception {
                MyObject loadedObject = (MyObject) currentSystem().getIdentifiable(objectId);
                EntityReference ref = (EntityReference) currentSystem().getSimpleObject();
                assertSame(loadedObject, ref.object);

                return null;
            }
        });
    }

    private Object withCurrentPrevayler(PrevaylerAction prevaylerAction) throws Exception {
        return CurrentPrevayler.withPrevayler(currentPrevayler, prevaylerAction);
    }

    private Object newInstance(final Class mainClass) {
        try {
            return withCurrentPrevayler(new PrevaylerAction() {
                public Object run() throws Exception {
                    return currentSystem().newInstance(mainClass);
                }
            });
        } catch (Exception e) {
            fail();
            return null;
        }
    }


    /**
     * @entity
     */
    public static interface CallingBack {
        /**
         * @transaction
         */
        void callback(CalledBack arg, String valueMadePersistent);
    }

    public static class CallingBackImpl implements CallingBack, Serializable {
        public void callback(CalledBack arg, String valueMadePersistent) {
            arg.setValue(valueMadePersistent);
            arg.setCallingBack((CallingBack) Aspects.getThis());
        }
    }

    /**
     * @entity
     */
    public static interface CalledBack {
        /**
         * @transaction
         */
        void setValue(String value);

        String getValue();

        /**
         * @transaction
         */
        void setCallingBack(CallingBack callingBack);

        CallingBack getCallingBack();
    }

    public static class CalledBackImpl implements CalledBack, Serializable {
        String value;
        CallingBack callingBack;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public CallingBack getCallingBack() {
            return callingBack;
        }

        public void setCallingBack(CallingBack callingBack) {
            this.callingBack = callingBack;
        }
    }


    public void testMethodCallingBackOnArgumentIsMadePersistent() throws Exception {
        newPrevayler();

        final CallingBack callingBack = (CallingBack) newInstance(CallingBack.class);
        final CalledBack calledBack = (CalledBack) newInstance(CalledBack.class);

        withCurrentPrevayler(new PrevaylerAction() {
            public Object run() throws Exception {
                currentSystem().add(callingBack);
                currentSystem().add(calledBack);

                callingBack.callback(calledBack, "value");
                assertEquals("value", calledBack.getValue());
                return null;
            }
        });

        final long calledBackId = getObjectId(calledBack);
        final long callingBackId = getObjectId(callingBack);

        newPrevayler();
        withCurrentPrevayler(new PrevaylerAction() {
            public Object run() throws Exception {
                CalledBack calledBack = (CalledBack) currentSystem().getIdentifiable(calledBackId);
                CallingBack callingBack = (CallingBack) currentSystem().getIdentifiable(callingBackId);
                assertEquals("value", calledBack.getValue());
                assertSame(callingBack, calledBack.getCallingBack());
                return null;
            }
        });
    }

    /**
     * @entity
     */
    public interface ObjectWithValue {
        String getValue();

        /**
         * @transaction
         */
        void setValue(String freddl);
    }

    public static class ObjectWithValueImpl implements ObjectWithValue, Serializable {
        String value = "initialValue";

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public void testChangeAndReturnPreviousValue() throws Exception {
        // JIRA: NAN-11
        newPrevayler();
        withCurrentPrevayler(new PrevaylerAction() {
            public Object run() throws Exception {
                ObjectWithValue o = (ObjectWithValue) newInstance(ObjectWithValue.class);
                assertEquals("initialValue", o.getValue());
                assertEquals("initialValue", currentSystem().changeAndReturnPreviousValue(o, "FREDDL"));
                return null;
            }
        });
    }

    /**
     * @entity
     */
    public interface TransactionRequired {
        /**
         * @transaction-required
         */
        public void transactionRequired();
    }

    public static class TransactionRequiredImpl implements TransactionRequired {
        public void transactionRequired() {
            fail("should not have been executed");
        }
    }

    public void testCallsToMethodWithTransactionRequiredFailsOutsideATransaction() {
        TransactionRequired transactionRequired = (TransactionRequired) aspectSystem.newInstance(TransactionRequired.class);
        try {
            transactionRequired.transactionRequired();
        } catch (TransactionRequiredException e) {
        }
    }

    /**
     * Big badass functional test. I hate it, I want to kill it... --jon
     */
//    public void test() throws Exception {
//        newPrevayler();
//        withCurrentPrevayler(new PrevaylerAction() {
//            public Object run() throws NoSuchMethodException {
//                MyObject insideObject = currentSystem().createMyObject();
//                currentPrevayler.assertTransactionLog("newInstance");
//                currentSystem().setMyObject(insideObject);
//                currentPrevayler.assertTransactionLog("newInstance setMyObject");
//
//                assertTrue("Prevayler is lost after first transaction", CurrentPrevayler.hasPrevayler());
//                insideObject.setValue("oldValue");
//                currentPrevayler.assertNumberOfCommands(3);
//                insideObject.setValue("newValue");
//                currentPrevayler.assertNumberOfCommands(4);
//
//                MyObject outsideObject = (MyObject) currentSystem().newInstance(MyObject.class);
//                currentPrevayler.assertNumberOfCommands("no command when object created outside prevayler", 4);
//
//                MyObject outsideNestedObject = (MyObject) currentSystem().newInstance(MyObject.class);
//                currentPrevayler.assertNumberOfCommands("no command when object created outside prevayler", 4);
//
//                outsideObject.setMyObject(outsideNestedObject);
//                currentPrevayler.assertNumberOfCommands("commands operating on objects outside prevayler still generates " +
//                        "commands (they shouldn't really but we haven't implemented that yet)",
//                        5);
//
//                // mixing prevayler and non-prevayler stuff the other way around will mess things up
//                // uncomment this line and watch the nice little assert failure
//                // there's another line in checkMySystem() that explains the real problem
//                //                outsideNestedObject.setMyObject(insideObject);
//
//                insideObject.setMyObject(outsideObject);
//                currentPrevayler.assertNumberOfCommands(
//                        "command when operating on object inside prevayler with object outside prevayler", 6);
//
//                Collection objects = currentSystem().getAllObjects();
//                assertEquals("objects not created ", 2, objects.size());
//                final MyObject objectToFind = insideObject;
//                insideObject = (MyObject) CollectionUtils.find(objects, new Predicate() {
//                    public boolean evaluate(Object o) {
//                        return o == objectToFind;
//                    }
//                });
//                assertNotNull(insideObject);
//                assertEquals("value not correct", "newValue", insideObject.getValue());
//                assertNotNull(insideObject.getMyObject());
//
//                return null;
//            }
//        });
//
//        // reload database
//        checkMySystem();
//
//        // reload database with snapshot
//        currentPrevayler.takeSnapshot();
//        checkMySystem();
//    }
//
//    private void checkMySystem() throws Exception {
//        newPrevayler();
//        withCurrentPrevayler(new PrevaylerAction() {
//            public Object run() {
//                Collection objects = currentSystem().getAllObjects();
//                assertEquals("objects not persisted", 2, objects.size());
//                MyObject myObject = currentSystem().getMyObject();
//                assertEquals("property not correct value", "newValue", myObject.getValue());
//                currentPrevayler.assertNumberOfCommands("just checking, should be no commands", 0);
//
//                // this will not hold if you mix objects created inside prevayler with those created outside
//                // this object will not be identified correctly in the command and you will have two copies of the
//                // "same" object
//                //                assertSame(myObject, myObject.getMyObject().getMyObject().getMyObject());
//                return null;
//            }
//        });
//    }

    public void testABC() throws Exception {
        AspectInstance aspectInstance = Aspects.getAspectInstance(aspectSystem.newInstance(MyObject.class));
        Mixin mixinInstance = (Mixin) aspectInstance.getMixins().iterator().next();

        Method setValue = MyObject.class.getDeclaredMethod("setValue", new Class[]{String.class});
        assertEquals(2, mixinInstance.getInterceptorsForMethod(setValue).size());

        Method setABC = MyObject.class.getDeclaredMethod("setABC", new Class[]{String[].class});
        assertTrue(Attributes.getAttributes(MyObject.class).hasAttribute(setABC, "transaction"));
        assertTrue(Attributes.hasAttribute(setABC, "transaction"));
        assertEquals(2, mixinInstance.getInterceptorsForMethod(setABC).size());

        newPrevayler();
        withCurrentPrevayler(new PrevaylerAction() {
            public Object run() {
                MyObject myObject = currentSystem().createMyObject();
                currentPrevayler.assertNumberOfCommands(1);
                myObject.setABC(null);
                currentPrevayler.assertNumberOfCommands(2);
                return null;
            }
        });
    }

    public void testGarbageCollect() throws Exception {
        newPrevayler();
        withCurrentPrevayler(new PrevaylerAction() {
            public Object run() {
                MyObject myObject = (MyObject) currentSystem().newInstance(MyObject.class);
                currentSystem().setMyObject(myObject);
                myObject.setMyObject((MyObject) currentSystem().newInstance(MyObject.class));
                assertEquals("two MyObjects should have been created", 2, currentSystem().getAllObjects().size());
                return null;
            }
        });

        // restoring
        currentPrevayler.takeSnapshot();
        newPrevayler();
        withCurrentPrevayler(new PrevaylerAction() {
            public Object run() {
                assertEquals("two MyObjects should be left", 2, currentSystem().getAllObjects().size());
                // removing one of the objects
                assertNotNull(currentSystem().getMyObject().getMyObject());
                currentSystem().getMyObject().setMyObject(null);
                return null;
            }
        });

        // restoring and garbage collecting
        currentPrevayler.takeSnapshot();
        newPrevayler();
        withCurrentPrevayler(new PrevaylerAction() {
            public Object run() {
                assertNull(currentSystem().getMyObject().getMyObject());
                assertEquals("1 MyObjects should be left, one garbage collected", 1, currentSystem().getAllObjects().size());
                return null;
            }
        });
    }

    public void testSerialization() throws IOException, ClassNotFoundException {
        MyObject myObject = (MyObject) aspectSystem.newInstance(MyObject.class);
        myObject.setValue("value");
        HashMap identityHashMap = new HashMap();
        identityHashMap.put(myObject, new Long(1));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(identityHashMap);
        objectOutputStream.close();
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
        identityHashMap = (HashMap) objectInputStream.readObject();

        myObject = (MyObject) identityHashMap.keySet().iterator().next();
        assertEquals("value", myObject.getValue());
    }

    private MySystem currentSystem() {
        return CurrentPrevayler.hasPrevayler()
                ? (MySystem) CurrentPrevayler.getSystem()
                : (MySystem) currentPrevayler.prevalentSystem();
    }

    private void newPrevayler() throws IOException, ClassNotFoundException {
        currentPrevayler = new CountingPrevayler(
                PrevaylerFactory.createPrevayler((Serializable) Aspects.getCurrentAspectFactory().newInstance(MySystem.class),
                                                 prevaylerDir.getAbsolutePath()));
    }

    public interface TestUnsupportedTransaction {
        /**
         * @transaction-unsupported
         */
        void callWithUnsupportedTransaction();
    }

    public static class TestUnsupportedTransactionImpl implements TestUnsupportedTransaction {
        public void callWithUnsupportedTransaction() {
            Assert.assertFalse(CheckTransactionUnsupportedInterceptor.isTransactionsSupported());
            MyObject myObject = (MyObject) Aspects.getCurrentAspectFactory().newInstance(MyObject.class);
            myObject.setValue("test"); // this call should not be permitted
        }
    }

    public void testUnsupportedTransaction() {
        assertTrue(CheckTransactionUnsupportedInterceptor.isTransactionsSupported());
        TestUnsupportedTransaction testUnsupportedTransaction =
                (TestUnsupportedTransaction) aspectSystem.newInstance(TestUnsupportedTransaction.class);
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

