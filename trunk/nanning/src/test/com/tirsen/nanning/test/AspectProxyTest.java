/*
 * Angkor Web Framework
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.test;

import junit.framework.TestCase;
import com.tirsen.nanning.AspectProxy;

/**
 * TODO document AspectProxyTest
 *
 * <!-- $Id: AspectProxyTest.java,v 1.1.1.1 2002-10-20 09:33:53 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1.1.1 $
 */
public class AspectProxyTest extends TestCase
{
    public static class Impl implements Intf
    {
        private boolean called;

        public void call()
        {
            called = true;
        }

        public void verify()
        {
            assertTrue(called);
        }
    }

    public void testAspectProxy()
    {
        Impl impl = new Impl();

        AspectProxy aspectProxy = AspectProxy.create(impl);
        aspectProxy.setInterfaceClass(Intf.class);

        MockAspect aspect = new MockAspect();
        aspectProxy.addAspect(aspect);
        MockAspect aspect2 = new MockAspect();
        aspectProxy.addAspect(aspect2);

        Intf intf = (Intf) aspectProxy.getProxy();

        aspect.expectRealObject(impl);
        aspect.expectProxy(intf);
        aspect2.expectRealObject(impl);
        aspect2.expectProxy(intf);

        intf.call();
        impl.verify();
        aspect.verify();
        aspect2.verify();
    }

    public void testPerformanceAndMemory()
    {
        Impl impl = new Impl();

        AspectProxy aspectProxy = AspectProxy.create(impl);
        aspectProxy.setInterfaceClass(Intf.class);

        MockAspect aspect = new MockAspect();
        aspectProxy.addAspect(aspect);
        MockAspect aspect2 = new MockAspect();
        aspectProxy.addAspect(aspect2);

        Intf intf = (Intf) aspectProxy.getProxy();

        int numberOfInvocations = 1000000;
        double maxMemoryPerInvocation = 0.5;
        double maxTimePerInvocation = 0.003;

        ///CLOVER:OFF
        System.gc();
        long startMemory = Runtime.getRuntime().freeMemory();
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numberOfInvocations; i++)
        {
            intf.call();
        }

        long time = System.currentTimeMillis() - startTime;
        long memory = startMemory - Runtime.getRuntime().freeMemory();
        ///CLOVER:ON


        double timePerInvocation = time / (double) numberOfInvocations;
        double memoryPerInvocation = memory / (double) numberOfInvocations;

        System.out.println();
        System.out.println("time = " + time);
        System.out.println("memory = " + memory);
        System.out.println("memoryPerInvocation = " + memoryPerInvocation);
        System.out.println("timePerInvocation = " + timePerInvocation);

        assertTrue("memory per invocation exceeded", memoryPerInvocation < maxMemoryPerInvocation);
        assertTrue("time per invocation exceeded", timePerInvocation < maxTimePerInvocation);
    }
}
