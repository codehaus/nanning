/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.test;

import com.tirsen.nanning.AspectProxy;
import com.tirsen.nanning.Factory;
import junit.framework.TestCase;

/**
 * TODO document PerformanceTest
 *
 * <!-- $Id: PerformanceTest.java,v 1.1 2002-10-21 21:07:31 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public class PerformanceTest extends TestCase
{
    public void testPerformanceAndMemory()
    {
        Impl impl = new Impl();

        AspectProxy aspectProxy = AspectProxy.create(impl);

        NullAspect aspect = new NullAspect();
        aspectProxy.addAspect(aspect);
        NullAspect aspect2 = new NullAspect();
        aspectProxy.addAspect(aspect2);

        Intf intf = (Intf) aspectProxy.createProxy(new Class[] { Intf.class });

        int numberOfInvocations = 100000;
        double maxMemoryPerInvocation = 4;
        double maxTimePerInvocation = 0.003; // this is exceptionally high due to clover...

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

    public void testInstanceFootprint() throws IllegalAccessException, InstantiationException
    {
        int numberOfInstances = 1000;
        int timesBiggerTolerance = 8;

        // allocate a set of ordinary instances and check for footprint
        Impl[] impls = new Impl[numberOfInstances];

        ///CLOVER:OFF
        System.gc();
        long startMemory = Runtime.getRuntime().freeMemory();

        for (int i = 0; i < numberOfInstances; i++)
        {
            impls[i] = new Impl();
        }

        long memory = startMemory - Runtime.getRuntime().freeMemory();
        ///CLOVER:ON

        double memoryPerOrdinaryInstance = memory / (double) numberOfInstances;

        // determine max-memory per aspect-instance compared to ordinary instances
        double maxMemoryPerInstance = memoryPerOrdinaryInstance * timesBiggerTolerance;

        // setup a factory
        Factory.addFactory(Intf.class);
        Factory factory = Factory.getFactory(Intf.class);
        factory.setDefaultTarget(Impl.class);
        factory.addAspect(NullAspect.class);
        factory.addAspect(NullAspect.class);

        // instantiates a bunch of aspect-instances
        ///CLOVER:OFF
        System.gc();
        startMemory = Runtime.getRuntime().freeMemory();

        for (int i = 0; i < numberOfInstances; i++)
        {
            factory.newInstance();
        }

        memory = startMemory - Runtime.getRuntime().freeMemory();
        ///CLOVER:ON

        double memoryPerInstance = memory / (double) numberOfInstances;
        System.out.println();
        System.out.println("memory = " + memory);
        System.out.println("memoryPerOrdinaryInstance = " + memoryPerOrdinaryInstance);
        System.out.println("maxMemoryPerInstance = " + maxMemoryPerInstance);
        System.out.println("times bigger = " + memoryPerInstance / memoryPerOrdinaryInstance);
        System.out.println("memoryPerInstance = " + memoryPerInstance);

        assertTrue("memory per instance exceeded", memoryPerInstance < maxMemoryPerInstance);
    }
}
