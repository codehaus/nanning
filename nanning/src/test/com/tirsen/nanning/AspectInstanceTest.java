/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.lang.reflect.Proxy;
import java.lang.reflect.Method;

import junit.framework.TestCase;

/**
 * TODO document AspectClassTest
 *
 * <!-- $Id: AspectInstanceTest.java,v 1.6 2003-04-23 20:44:37 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.6 $
 */
public class AspectInstanceTest extends TestCase {
    public void testEmptyAspectInstance() {
        AspectInstance instance = new AspectInstance();
        Object proxy = instance.getProxy();
        // test some of the methods from java.lang.Object
        assertNotNull(proxy);
        assertNotNull(proxy.toString());
        assertSame(proxy, instance.getProxy());
    }

    public void testAspectInstanceWithOneMixin() {
        AspectInstance instance = new AspectInstance();
        instance.addMixin(new MixinInstance(Intf.class, new Impl()));
        Object proxy = instance.getProxy();
        assertTrue(proxy instanceof Intf);
        Intf intf = (Intf) proxy;
        intf.call();
    }

    Method calledMethod = null;
    public void testInterceptor() throws NoSuchMethodException {
        AspectInstance instance = new AspectInstance();
        MixinInstance mixin = new MixinInstance(Intf.class, new Impl());
        Method callMethod = Intf.class.getMethod("call", null);
        mixin.addInterceptor(callMethod, new MethodInterceptor() {
            public Object invoke(Invocation invocation) throws Throwable {
                calledMethod = invocation.getMethod();
                return invocation.invokeNext();
            }
        });
        instance.addMixin(mixin);
        Intf intf = (Intf) instance.getProxy();
        intf.call();
        assertEquals(callMethod, calledMethod);
    }


    public static class BlahongaException extends RuntimeException {
    }

    public static class BlahongaError extends Error {
    }

    public void testThrowsCorrectExceptions() {
        AspectInstance instance = new AspectInstance();
        MixinInstance mixin = new MixinInstance();
        mixin.setInterfaceClass(Intf.class);
        mixin.addInterceptor(new MockInterceptor());
        mixin.addInterceptor(new MockInterceptor());
        mixin.setTarget(new Impl());
        instance.addMixin(mixin);

        Intf proxy = (Intf) instance.getProxy();

        Aspects.setTarget(proxy, Intf.class, new Impl() {
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

        Aspects.setTarget(proxy, Intf.class, new Impl() {
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

    public void testGetRealClass() {
        assertSame(Intf.class,
                   Aspects.getRealClass(Proxy.getProxyClass(AspectInstanceTest.class.getClassLoader(), new Class[] { Intf.class })));
    }

    public void testSideAspectAndAspectsOnProxy() throws IllegalAccessException, InstantiationException, NoSuchMethodException {
        AspectInstance aspectInstance = new AspectInstance();
        MixinInstance mixinInstance = new MixinInstance();
        mixinInstance.setInterfaceClass(Intf.class);
        mixinInstance.addInterceptor(new MockInterceptor());
        mixinInstance.addInterceptor(new NullInterceptor());
        mixinInstance.setTarget(new Impl());
        aspectInstance.addMixin(mixinInstance);
        MixinInstance sideMixinInstance = new MixinInstance();
        sideMixinInstance.setInterfaceClass(TestMixin.class);
        sideMixinInstance.addInterceptor(new NullInterceptor());
        sideMixinInstance.addInterceptor(new MockInterceptor());
        sideMixinInstance.setTarget(new TestMixinImpl());
        aspectInstance.addMixin(sideMixinInstance);

        Object bigMomma = aspectInstance.getProxy();

        assertEquals(4, Aspects.getInterceptors(bigMomma).length);

        verifySideAspect(bigMomma);
    }

    public static void verifySideAspect(Object bigMomma) throws NoSuchMethodException {
        Impl target = (Impl) Aspects.getTarget(bigMomma, Intf.class);
        target.expectThis(bigMomma);
        MockInterceptor classInterceptor = (MockInterceptor) (Aspects.getInterceptors(bigMomma, Intf.class.getMethods()[0])[0]);
        classInterceptor.expectAtIndex(0);
        classInterceptor.expectNumberOfInterceptors(2);
        classInterceptor.expectCalledTimes(1);
        classInterceptor.expectProxy(bigMomma);
        classInterceptor.expectMethod(Intf.class.getMethod("call", null));
        classInterceptor.expectTarget(target);

        TestMixinImpl sideTarget = (TestMixinImpl) Aspects.getTarget(bigMomma, TestMixin.class);
        MockInterceptor sideInterceptor = (MockInterceptor) (Aspects.getInterceptors(bigMomma, TestMixin.class.getMethods()[0])[1]);
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
        MixinInstance mixinInstance = new MixinInstance();
        mixinInstance.setInterfaceClass(Intf.class);
        mixinInstance.setTarget(new Impl());
        aspectInstance.addMixin(mixinInstance);
        Intf intf = (Intf) aspectInstance.getProxy();

        Impl impl = (Impl) Aspects.getTarget(intf, Intf.class);

        intf.call();
        impl.verify();
    }

    public static class ImplWithEquals extends Impl {
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

//    public void testCallsOnJavaLangObject() {
//        AspectInstance aspectInstance1 = new AspectInstance();
//        aspectInstance1.addMixin(new MixinInstance(Intf.class, new ImplWithEquals("state")));
//        AspectInstance aspectInstance2 = new AspectInstance();
//        aspectInstance2.addMixin(new MixinInstance(Intf.class, new ImplWithEquals("state")));
//        assertEquals(aspectInstance1.getProxy(), aspectInstance2.getProxy());
//    }

}
