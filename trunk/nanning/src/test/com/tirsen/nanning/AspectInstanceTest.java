/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import junit.framework.TestCase;

/**
 * TODO document AspectClassTest
 *
 * <!-- $Id: AspectInstanceTest.java,v 1.1 2003-01-12 13:25:40 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public class AspectInstanceTest extends TestCase
{
    public static class BlahongaException extends RuntimeException
    {
    }

    public void testThrowsCorrectExceptions()
    {
        AspectInstance aspectInstance = new AspectInstance();
        MixinInstance mixinInstance = new MixinInstance();
        mixinInstance.setInterfaceClass(Intf.class);
        mixinInstance.addInterceptor(new MockInterceptor());
        mixinInstance.addInterceptor(new MockInterceptor());
        mixinInstance.setTarget(new Impl());
        aspectInstance.addMixin(mixinInstance);

        Intf proxy = (Intf) aspectInstance.getProxy();

        Aspects.setTarget(proxy, Intf.class, new Impl()
        {
            public void call()
            {
                throw new BlahongaException();
            }
        });

        try
        {
            proxy.call();
            fail();
        }
        catch (BlahongaException shouldHappen)
        {
        }
        catch (Exception e)
        {
            fail();
        }
    }

    public void testSideAspectAndAspectsOnProxy() throws IllegalAccessException, InstantiationException, NoSuchMethodException
    {
        AspectInstance aspectInstance = new AspectInstance();
        MixinInstance mixinInstance = new MixinInstance();
        mixinInstance.setInterfaceClass(Intf.class);
        mixinInstance.addInterceptor(new MockInterceptor());
        mixinInstance.addInterceptor(new NullInterceptor());
        mixinInstance.setTarget(new Impl());
        aspectInstance.addMixin(mixinInstance);
        MixinInstance sideMixinInstance = new MixinInstance();
        sideMixinInstance.setInterfaceClass(SideAspect.class);
        sideMixinInstance.addInterceptor(new NullInterceptor());
        sideMixinInstance.addInterceptor(new MockInterceptor());
        sideMixinInstance.setTarget(new SideAspectImpl());
        aspectInstance.addMixin(sideMixinInstance);

        Object bigMomma = aspectInstance.getProxy();

        assertEquals(4, Aspects.getInterceptors(bigMomma).length);

        verifySideAspect(bigMomma);
    }

    public static void verifySideAspect(Object bigMomma) throws NoSuchMethodException
    {
        Impl target = (Impl) Aspects.getTarget(bigMomma, Intf.class);
        target.expectThis(bigMomma);
        MockInterceptor classInterceptor = (MockInterceptor) (Aspects.getInterceptors(bigMomma, Intf.class.getMethods()[0])[0]);
        classInterceptor.expectAtIndex(0);
        classInterceptor.expectNumberOfInterceptors(2);
        classInterceptor.expectCalledTimes(1);
        classInterceptor.expectProxy(bigMomma);
        classInterceptor.expectMethod(Intf.class.getMethod("call", null));
        classInterceptor.expectTarget(target);

        SideAspectImpl sideTarget = (SideAspectImpl) Aspects.getTarget(bigMomma, SideAspect.class);
        MockInterceptor sideInterceptor = (MockInterceptor) (Aspects.getInterceptors(bigMomma, SideAspect.class.getMethods()[0])[1]);
        sideInterceptor.expectAtIndex(1);
        sideInterceptor.expectNumberOfInterceptors(2);
        sideInterceptor.expectCalledTimes(1);
        sideInterceptor.expectProxy(bigMomma);
        sideInterceptor.expectMethod(SideAspect.class.getMethod("sideCall", null));
        sideInterceptor.expectTarget(sideTarget);

        // this calls the class-target and the class-interceptor
        ((Intf) bigMomma).call();
        // this calls the side-target, the class-interceptor and the side-interceptor
        classInterceptor.expectTarget(null);
        classInterceptor.expectMethod(null);
        classInterceptor.expectNumberOfInterceptors(2);
        ((SideAspect) bigMomma).sideCall();

        classInterceptor.verify();
        target.verify();
        sideInterceptor.verify();
        sideTarget.verify();
    }

    public void testNoAspects() throws IllegalAccessException, InstantiationException
    {
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
