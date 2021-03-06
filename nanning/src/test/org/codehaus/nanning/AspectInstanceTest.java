/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.codehaus.nanning;

import junit.framework.TestCase;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * TODO document AspectClassTest
 *
 * <!-- $Id: AspectInstanceTest.java,v 1.2 2003-07-12 16:48:16 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.2 $
 */
public class AspectInstanceTest extends TestCase {
    private Method callMethod;
//    private Method mainCallMethod;

    protected void setUp() throws Exception {
        super.setUp();

        callMethod = Interface.class.getMethod("call", null);
//        mainCallMethod = TestObject.class.getMethod("mainCall", null);
    }

    public void testEmptyAspectInstance() {
        AspectInstance instance = new AspectInstance();
        Object proxy = instance.getProxy();
        // test some of the methods from java.lang.Object
        assertNotNull(proxy);
        assertNotNull(proxy.toString());
        assertSame(proxy, instance.getProxy());
        assertEquals(0, instance.getTargets().length);
    }

    public static interface Interface {
        void call();
    }

    public static class Target implements Interface {
        public void call() {
        }
    }

    public void testAspectInstanceWithOneMixin() {
        AspectInstance instance = new AspectInstance();
        Target target = new Target();
        instance.addMixin(new Mixin(Interface.class, target));
        Object proxy = instance.getProxy();
        assertTrue(proxy instanceof Interface);
        Interface intf = (Interface) proxy;
        intf.call();
        assertNotNull(proxy.toString());
        assertSame(target, instance.getTargets()[0]);
    }

    boolean wasCalled = false;

    public void testAddedInterceptorIsInvoked() throws NoSuchMethodException {
        AspectInstance instance = new AspectInstance();

        final Target target = new Target();
        Mixin mixin = new Mixin(Interface.class, target);
        instance.addMixin(mixin);
        final Interface intf = (Interface) instance.getProxy();

        mixin.addInterceptor(callMethod, new MethodInterceptor() {
            public Object invoke(Invocation invocation) throws Throwable {
                wasCalled = true;
                assertEquals(callMethod, invocation.getMethod());
                assertSame(target, invocation.getTarget());
                assertSame(intf, invocation.getProxy());
                return invocation.invokeNext();
            }
        });

        intf.call();
        assertTrue(wasCalled);
    }

    public void testConstructors() {
        AspectInstance aspectInstance = new AspectInstance();
        assertNull(aspectInstance.getClassIdentifier());
        assertNull(aspectInstance.getAspectFactory());

        AspectFactory aspectFactory = new NullAspectFactory();
        aspectInstance = new AspectInstance(aspectFactory, Interface.class);
        assertSame(aspectFactory, aspectInstance.getAspectFactory());
        assertSame(Interface.class, aspectInstance.getClassIdentifier());
    }

    public void testChangeTarget() {
        AspectInstance instance = new AspectInstance();
        Mixin mixin = new Mixin(Interface.class, new Target());
        instance.addMixin(mixin);
        Target target = new Target();
        mixin.setTarget(target);
        assertSame(target, mixin.getTarget());
    }

    public void testInvocationOnTarget() {
        AspectInstance instance = new AspectInstance();
        Mixin mixin = new Mixin(Interface.class, null);
        instance.addMixin(mixin);

        final Interface proxy = (Interface) instance.getProxy();
        mixin.setTarget(new Target() {
            public void call() {
                wasCalled = true;
                assertSame(proxy, Aspects.getThis());
            }
        });

        assertFalse(wasCalled);
        proxy.call();
        assertTrue(wasCalled);
    }

    public void testChangeTargetDuringInterception() {
        AspectInstance instance = new AspectInstance();
        Mixin mixin = new Mixin(Interface.class, new Target());
        instance.addMixin(mixin);

        final Target target = new Target();
        mixin.setTarget(target);
        mixin.addInterceptor(new MethodInterceptor() {
            public Object invoke(Invocation invocation) throws Throwable {
                invocation.setTarget(target);
                return invocation.invokeNext();
            }
        });
        assertSame(target, mixin.getTarget());
    }

    public static interface EmptyInterface {
    }

    public void testClassIdentifierAsInterfaceInProxy() {
        AspectInstance instance = new AspectInstance(EmptyInterface.class);
        assertTrue(instance.getProxy() instanceof EmptyInterface);
    }

    public void testGetInterceptors() {
        AspectInstance instance = new AspectInstance();
        Mixin mixin = new Mixin(Interface.class, new Target());
        MethodInterceptor interceptor = new NOPInterceptor();
        mixin.addInterceptor(interceptor);
        instance.addMixin(mixin);

        assertTrue(instance.getAllInterceptors().contains(interceptor));

        Mixin mixin2 = new Mixin(EmptyInterface.class, null);
        MethodInterceptor interceptor2 = new NOPInterceptor();
        mixin2.addInterceptor(interceptor2);
        assertTrue(instance.getInterceptors(Interface.class).contains(interceptor));
        assertFalse(instance.getInterceptors(Interface.class).contains(interceptor2));
    }

    public void testGetMixin() {
        AspectInstance instance = new AspectInstance();
        Mixin mixin = new Mixin(Interface.class, new Target());
        instance.addMixin(mixin);
        assertSame(mixin, instance.getMixinForInterface(Interface.class));
        assertTrue(instance.hasMixinForInterface(Interface.class));
    }

    public static interface Base {
    }

    public static interface Sub extends Base {
    }

    public void testGetMixinWithInheritance() {
        AspectInstance instance = new AspectInstance();
        Mixin mixin = new Mixin(Sub.class, null);
        instance.addMixin(mixin);
        assertSame(mixin, instance.getMixinForInterface(Base.class));
    }

//    public void testEqualsAndHashCode() {
//        AspectInstance instance = new AspectInstance();
//        AspectInstance instance2 = new AspectInstance();
//        assertEqualsAndHashCodeEquals(instance, instance2);
//
//        NullAspectFactory aspectFactory = new NullAspectFactory();
//        instance = new AspectInstance(aspectFactory, Interface.class);
//        instance2 = new AspectInstance(aspectFactory, Interface.class);
//        assertEqualsAndHashCodeEquals(instance, instance2);
//
//        MixinInstance mixin = new MixinInstance(Interface.class, null);
//        MixinInstance mixin2 = new MixinInstance(Interface.class, null);
//        assertEqualsAndHashCodeEquals(mixin, mixin2);
//        instance.addMixin(mixin);
//        instance2.addMixin(mixin2);
//        assertEqualsAndHashCodeEquals(instance, instance2);
//    }

//    private void assertEqualsAndHashCodeEquals(Object o1, Object o2) {
//        assertEquals(o1, o2);
//        assertEquals(o1.hashCode(), o2.hashCode());
//    }


    public static class BlahongaException extends RuntimeException {
    }

    public static class BlahongaError extends Error {
    }

    public void testThrowsCorrectExceptions() {
        AspectInstance instance = new AspectInstance();
        Mixin mixin = new Mixin(Interface.class, null);
        instance.addMixin(mixin);

        Interface proxy = (Interface) instance.getProxy();

        mixin.setTarget(new Interface() {
            public void call() {
                throw new BlahongaException();
            }
        });

        try {
            proxy.call();
            fail();
        } catch (BlahongaException shouldHappen) {
        } catch (Exception e) {
            fail();
        }

        mixin.setTarget(new Interface() {
            public void call() {
                throw new BlahongaError();
            }
        });

        try {
            proxy.call();
            fail();
        } catch (BlahongaError shouldHappen) {
        } catch (Exception e) {
            fail();
        }
    }

    public static class BlahongaCheckedException extends RuntimeException {
    }

    public static interface InterfaceWithException {
        void call() throws BlahongaCheckedException;
    }

    public void testThrowCheckedException() {
        AspectInstance instance = new AspectInstance();
        Mixin mixin = new Mixin(InterfaceWithException.class, null);
        instance.addMixin(mixin);

        InterfaceWithException proxy = (InterfaceWithException) instance.getProxy();

        mixin.setTarget(new InterfaceWithException() {
            public void call() throws BlahongaCheckedException {
                throw new BlahongaCheckedException();
            }
        });

        try {
            proxy.call();
            fail();
        } catch (BlahongaCheckedException e) {
        } catch (Throwable e) {
            fail();
        }
    }


    public void testGetRealClass() {
        assertSame(Intf.class,
                Aspects.getRealClass(Proxy.getProxyClass(AspectInstanceTest.class.getClassLoader(), new Class[]{Intf.class})));
    }

    public void testAddInterceptor() {
        MethodInterceptor interceptor = new MethodInterceptor() {
                public Object invoke(Invocation invocation) throws Throwable {
                    return null;
                }
            };
        AspectInstance instance = new AspectInstance();
        instance.addMixin(new Mixin(Interface.class, new Target()));
        instance.addInterceptor(interceptor);
        assertTrue(instance.getInterceptorsForMethod(callMethod).contains(interceptor));
    }



    public void testSideAspectAndAspectsOnProxy() throws IllegalAccessException, InstantiationException, NoSuchMethodException {
        AspectInstance aspectInstance = new AspectInstance();
        Mixin mixinInstance = new Mixin();
        mixinInstance.setInterfaceClass(Intf.class);
        mixinInstance.addInterceptor(new MockInterceptor());
        mixinInstance.addInterceptor(new NullInterceptor());
        mixinInstance.setTarget(new IntfImpl());
        aspectInstance.addMixin(mixinInstance);
        Mixin sideMixinInstance = new Mixin();
        sideMixinInstance.setInterfaceClass(TestMixin.class);
        sideMixinInstance.addInterceptor(new NullInterceptor());
        sideMixinInstance.addInterceptor(new MockInterceptor());
        sideMixinInstance.setTarget(new TestMixinImpl());
        aspectInstance.addMixin(sideMixinInstance);

        Object bigMomma = aspectInstance.getProxy();

        assertEquals(4, Aspects.getInterceptors(bigMomma).size());

        verifySideAspect(bigMomma);
    }

    public static void verifySideAspect(Object bigMomma) throws NoSuchMethodException {
        IntfImpl target = (IntfImpl) Aspects.getTarget(bigMomma, Intf.class);
        target.expectThis(bigMomma);
        MockInterceptor classInterceptor = (MockInterceptor) (Aspects.getAspectInstance(bigMomma).getInterceptorsForMethod(Intf.class.getMethods()[0]).get(0));
        classInterceptor.expectAtIndex(0);
        classInterceptor.expectNumberOfInterceptors(2);
        classInterceptor.expectCalledTimes(1);
        classInterceptor.expectProxy(bigMomma);
        classInterceptor.expectMethod(Intf.class.getMethod("call", null));
        classInterceptor.expectTarget(target);

        TestMixinImpl sideTarget = (TestMixinImpl) Aspects.getTarget(bigMomma, TestMixin.class);
        MockInterceptor sideInterceptor = (MockInterceptor) (Aspects.getAspectInstance(bigMomma).getInterceptorsForMethod(TestMixin.class.getMethods()[0]).get(1));
        sideInterceptor.expectAtIndex(1);
        sideInterceptor.expectNumberOfInterceptors(2);
        sideInterceptor.expectCalledTimes(1);
        sideInterceptor.expectProxy(bigMomma);
        sideInterceptor.expectMethod(TestMixin.class.getMethod("mixinCall", null));
        sideInterceptor.expectTarget(sideTarget);

        // this calls the class-target and the class-interceptor
        ((Intf) bigMomma).call();
        // this calls the side-target, the class-interceptor and the side-interceptor
        classInterceptor.expectTarget(null);
        classInterceptor.expectMethod(null);
        classInterceptor.expectNumberOfInterceptors(2);
        ((TestMixin) bigMomma).mixinCall();

        classInterceptor.verify();
        target.verify();
        sideInterceptor.verify();
        sideTarget.verify();
    }

    public void testNoInterceptors() throws IllegalAccessException, InstantiationException {
        AspectInstance aspectInstance = new AspectInstance();
        Mixin mixinInstance = new Mixin();
        mixinInstance.setInterfaceClass(Intf.class);
        mixinInstance.setTarget(new IntfImpl());
        aspectInstance.addMixin(mixinInstance);
        Intf intf = (Intf) aspectInstance.getProxy();

        IntfImpl impl = (IntfImpl) Aspects.getTarget(intf, Intf.class);

        intf.call();
        impl.verify();
    }

    public static class ImplWithEquals extends IntfImpl {
        String state;

        public ImplWithEquals(String state) {
            this.state = state;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ImplWithEquals)) return false;

            final ImplWithEquals implWithEquals = (ImplWithEquals) o;

            if (!state.equals(implWithEquals.state)) return false;

            return true;
        }

        public int hashCode() {
            return state.hashCode();
        }
    }

    private static class NOPInterceptor implements MethodInterceptor {
        public Object invoke(Invocation invocation) throws Throwable {
            return invocation.invokeNext();
        }
    }

    private static class NullAspectFactory implements AspectFactory {
        public Object newInstance(Class classIdentifier) {
            return null;
        }

        public void reinitialize(AspectInstance aspectInstance) {
        }
    }

//    public void testCallsOnJavaLangObject() {
//        AspectInstance aspectInstance1 = new AspectInstance();
//        aspectInstance1.addMixin(new MixinInstance(Intf.class, new ImplWithEquals("state")));
//        AspectInstance aspectInstance2 = new AspectInstance();
//        aspectInstance2.addMixin(new MixinInstance(Intf.class, new ImplWithEquals("state")));
//        assertEquals(aspectInstance1.getProxy(), aspectInstance2.getProxy());
//    }

//    public static class TestObject {
//        boolean wasCalled;
//        Object thisDuringCall;
//
//        public void mainCall() {
//            wasCalled = true;
//            thisDuringCall = this;
//        }
//    }

//    public void testInterceptOrdinaryClass() {
//        final TestObject target = new TestObject();
//        MixinInstance mainMixin = new MixinInstance(TestObject.class, target);
//        assertTrue(mainMixin.isMainMixin());
//        MixinInstance mixin = new MixinInstance(Interface.class, Target.class);
//        assertFalse(mixin.isMainMixin());
//        AspectInstance instance = new AspectInstance();
//        instance.addMixin(mainMixin);
//        instance.addMixin(mixin);
//
//        Object proxy = instance.getProxy();
//        assertTrue(proxy instanceof TestObject);
//        final TestObject testObject = (TestObject) proxy;
//
//        // ordinary call
//        assertFalse(target.wasCalled);
//        testObject.mainCall();
//        assertTrue(target.wasCalled);
//        assertFalse(testObject == testObject.thisDuringCall);
//        assertSame(target, target.thisDuringCall);
//        target.wasCalled= false;
//
//        // call with interceptor
//        assertEquals(0, mainMixin.getInterceptorsForMethod(mainCallMethod).size());
//        mainMixin.addInterceptor(new MethodInterceptor() {
//            public Object invoke(Invocation invocation) throws Throwable {
//                wasCalled = true;
//                assertSame(testObject, invocation.getProxy());
//                assertSame(target, invocation.getTarget());
//                return invocation.invokeNext();
//            }
//        });
//        assertEquals(1, mainMixin.getInterceptorsForMethod(mainCallMethod).size());
//
//        assertFalse(wasCalled);
//        assertFalse(target.wasCalled);
//        testObject.mainCall();
//        assertTrue(wasCalled);
//        assertTrue(target.wasCalled);
//    }

//    public static class HelloWorld {
//        public void helloWorld() {
//            System.out.println("Hello world");
//        }
//    }

//    public void testSimpleSampleAspectifyAnything() {
//        AspectInstance instance = new AspectInstance();
//        instance.addMixin(new MixinInstance(HelloWorld.class, new HelloWorld()));
//        instance.addInterceptor(new MethodInterceptor() {
//            public Object invoke(Invocation invocation) throws Throwable {
//                System.out.println("Before " + invocation.getMethod());
//                Object result = invocation.invokeNext();
//                System.out.println("After " + invocation.getMethod());
//                return result;
//            }
//        });
//        HelloWorld helloWorld = (HelloWorld) instance.getProxy();
//        helloWorld.helloWorld();
//    }

}
