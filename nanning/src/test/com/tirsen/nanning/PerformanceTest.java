/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import com.tirsen.nanning.AspectClass;
import com.tirsen.nanning.Impl;
import com.tirsen.nanning.Intf;
import com.tirsen.nanning.NullInterceptor;
import junit.framework.TestCase;

/**
 * TODO document PerformanceTest
 *
 * <!-- $Id: PerformanceTest.java,v 1.1 2002-11-17 14:03:34 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public class PerformanceTest extends TestCase
{
    public void testPerformanceAndMemory() throws IllegalAccessException, InstantiationException
    {
        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(Intf.class);
        aspectClass.addInterceptor(NullInterceptor.class);
        aspectClass.addInterceptor(NullInterceptor.class);
        aspectClass.setTarget(Impl.class);

        Intf intf = (Intf) aspectClass.newInstance();

        int numberOfInvocations = 100000;
        // these are exceptionally high due to Clover...
        double maxMemoryPerInvocation = 1.2;
        double maxTimePerInvocation = 0.005;

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
        int timesBiggerTolerance = 6;

        // allocate a set of ordinary instances and check for footprint
        Impl[] impls = new Impl[numberOfInstances];

        ///CLOVER:OFF
        System.gc();
        long startMemory = Runtime.getRuntime().freeMemory();

        for (int i = 0; i < numberOfInstances; i++)
        {
            impls[i] = new Impl();
        }

        System.gc();
        long memory = startMemory - Runtime.getRuntime().freeMemory();
        ///CLOVER:ON

        double memoryPerOrdinaryInstance = memory / (double) numberOfInstances;

        // determine max-memory per aspect-instance compared to ordinary instances
        double maxMemoryPerInstance = memoryPerOrdinaryInstance * timesBiggerTolerance;

        // setup a factory
        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(Intf.class);
        aspectClass.addInterceptor(NullInterceptor.class);
        aspectClass.addInterceptor(NullInterceptor.class);
        aspectClass.setTarget(Impl.class);

        // instantiates a bunch of aspect-instances
        ///CLOVER:OFF
        System.gc();
        startMemory = Runtime.getRuntime().freeMemory();

        Object[] objects = new Object[numberOfInstances];
        for (int i = 0; i < numberOfInstances; i++)
        {
            objects[i] = aspectClass.newInstance();
        }

        System.gc();
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