/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.lang.reflect.Proxy;

import junit.framework.TestCase;

/**
 * TODO document AspectClassTest
 *
 * <!-- $Id: AspectInstanceTest.java,v 1.5 2003-04-14 17:32:58 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.5 $
 */
public class AspectInstanceTest extends TestCase {
    public void testEmptyAspectInstance() {
        AspectInstance instance = new AspectInstance();
        Object proxy = instance.getProxy();
        // test some of the methods from java.lang.Object
        assertNotNull(proxy);
        assertNotNull(proxy.toString());
    }

    public void testAspectInstanceWithOneMixin() {
        AspectInstance instance = new AspectInstance();
        instance.addMixin(new MixinInstance(Intf.class, new Impl()));
        Object proxy = instance.getProxy();
        assertTrue(proxy instanceof Intf);
        Intf intf = (Intf) proxy;
        intf.call();
    }

    public static class BlahongaException extends RuntimeException {
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
}
