/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.test;

import junit.framework.TestCase;
import com.tirsen.nanning.*;

/**
 * TODO document AspectClassTest
 *
 * <!-- $Id: AspectClassTest.java,v 1.10 2002-11-06 17:50:06 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.10 $
 */
public class AspectClassTest extends TestCase
{
    public void testInterceptor() throws IllegalAccessException, InstantiationException, NoSuchMethodException
    {
        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(Intf.class);
        aspectClass.addInterceptor(MockInterceptor.class);
        aspectClass.addInterceptor(MockInterceptor.class);
        aspectClass.setTarget(Impl.class);

        Intf intf = (Intf) aspectClass.newInstance();
        Impl impl = (Impl) Aspects.getTarget(intf, Intf.class);

        Interceptor[] interceptors = Aspects.getInterceptors(intf, Intf.class);
        MockInterceptor aspect = (MockInterceptor) interceptors[0];
        MockInterceptor aspect2 = (MockInterceptor) interceptors[1];

        aspect.expectTarget(impl);
        aspect.expectProxy(intf);
        aspect.expectMethod(Intf.class.getMethod("call", null));
        aspect2.expectTarget(impl);
        aspect2.expectProxy(intf);
        aspect2.expectMethod(Intf.class.getMethod("call", null));

        intf.call();
        impl.verify();
        aspect.verify();
        aspect2.verify();
    }

    public static class BlahongaException extends RuntimeException
    {
    }

    public static interface ErrorIntf
    {
        void call() throws Exception;
    }

    public static class StatelessInterceptorImpl implements Interceptor, StatelessInterceptor
    {
        public Object invoke(Invocation invocation) throws Throwable
        {
            return invocation.invokeNext();
        }
    }

    public void testStatelessInterceptor()
    {
        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(Intf.class);
        aspectClass.addInterceptor(new InterceptorDefinition(StatelessInterceptorImpl.class));
        aspectClass.addInterceptor(new InterceptorDefinition(MockInterceptor.class));
        aspectClass.setTarget(Impl.class);

        Object proxy = aspectClass.newInstance();
        Interceptor statelessInterceptor = Aspects.getInterceptors(proxy)[0];
        Interceptor interceptor = Aspects.getInterceptors(proxy)[1];
        assertTrue(statelessInterceptor instanceof StatelessInterceptor);

        Object proxy2 = aspectClass.newInstance();
        Interceptor statelessInterceptor2 = Aspects.getInterceptors(proxy2)[0];
        Interceptor interceptor2 = Aspects.getInterceptors(proxy2)[1];
        assertSame(statelessInterceptor, statelessInterceptor2);
        assertNotSame(interceptor, interceptor2);
    }

    public static interface InheritedIntf extends Intf
    {
    }

    public static interface InheritedSideAspect extends Intf, SideAspect
    {
    }

    public void testInheritance()
    {
        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(InheritedIntf.class);
        aspectClass.addInterceptor(MockInterceptor.class);
        aspectClass.setTarget(Impl.class);
        AspectDefinition aspectDefinition = new AspectDefinition();
        // note that this interface also extends Intf, but that will never get called since
        // the class-aspect will take precedence
        aspectDefinition.setInterface(InheritedSideAspect.class);
        aspectDefinition.setTarget(SideAspectImpl.class);
        InheritedIntf proxy = (InheritedIntf) aspectClass.newInstance();

        MockInterceptor classInterceptor = (MockInterceptor) Aspects.getInterceptors(proxy)[0];
        classInterceptor.expectTarget(Aspects.getTarget(proxy, Intf.class));

        proxy.call();
    }

    public void testThrowsCorrectExceptions()
    {
        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(Intf.class);
        aspectClass.addInterceptor(MockInterceptor.class);
        aspectClass.addInterceptor(MockInterceptor.class);
        aspectClass.setTarget(Impl.class);

        Intf proxy = (Intf) aspectClass.newInstance();

        Aspects.setTarget(proxy, Intf.class, new Intf()
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
            System.out.println("shouldHappen = " + shouldHappen);
        }
        catch (Exception e)
        {
            fail();
        }
    }

    public void testSideAspectAndAspectsOnProxy() throws IllegalAccessException, InstantiationException, NoSuchMethodException
    {
        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(Intf.class);
        aspectClass.addInterceptor(MockInterceptor.class);
        aspectClass.addInterceptor(NullInterceptor.class);
        aspectClass.setTarget(Impl.class);
        AspectDefinition aspectDefinition = new AspectDefinition();
        aspectDefinition.setInterface(SideAspect.class);
        aspectDefinition.addInterceptor(NullInterceptor.class);
        aspectDefinition.addInterceptor(MockInterceptor.class);
        aspectDefinition.setTarget(SideAspectImpl.class);
        aspectClass.addSideAspect(aspectDefinition);

        Object bigMomma = aspectClass.newInstance();

        assertEquals(2, Aspects.getInterceptors(bigMomma).length);

        verifySideAspect(bigMomma);

    }

    public static void verifySideAspect(Object bigMomma) throws NoSuchMethodException
    {
        Impl target = (Impl) Aspects.getTarget(bigMomma, Intf.class);
        target.expectThis(bigMomma);
        MockInterceptor classInterceptor = (MockInterceptor) (Aspects.getInterceptors(bigMomma, Intf.class)[0]);
        classInterceptor.expectAtIndex(0);
        classInterceptor.expectNumberOfInterceptors(2);
        classInterceptor.expectCalledTimes(2);
        classInterceptor.expectProxy(bigMomma);
        classInterceptor.expectMethod(Intf.class.getMethod("call", null));
        classInterceptor.expectTarget(target);

        SideAspectImpl sideTarget = (SideAspectImpl) Aspects.getTarget(bigMomma, SideAspect.class);
        MockInterceptor sideInterceptor = (MockInterceptor) (Aspects.getInterceptors(bigMomma, SideAspect.class)[3]);
        sideInterceptor.expectAtIndex(3);
        sideInterceptor.expectNumberOfInterceptors(4);
        sideInterceptor.expectCalledTimes(1);
        sideInterceptor.expectProxy(bigMomma);
        sideInterceptor.expectMethod(SideAspect.class.getMethod("sideCall", null));
        sideInterceptor.expectTarget(sideTarget);

        // this calls the class-target and the class-interceptor
        ((Intf) bigMomma).call();
        // this calls the side-target, the class-interceptor and the side-interceptor
        classInterceptor.expectTarget(null);
        classInterceptor.expectMethod(null);
        classInterceptor.expectNumberOfInterceptors(4);
        ((SideAspect) bigMomma).sideCall();

        classInterceptor.verify();
        target.verify();
        sideInterceptor.verify();
        sideTarget.verify();
    }

    public void testNoAspects() throws IllegalAccessException, InstantiationException
    {
        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(Intf.class);
        aspectClass.setTarget(Impl.class);
        Intf intf = (Intf) aspectClass.newInstance();

        Impl impl = (Impl) Aspects.getTarget(intf, Intf.class);

        intf.call();
        impl.verify();
    }
}
