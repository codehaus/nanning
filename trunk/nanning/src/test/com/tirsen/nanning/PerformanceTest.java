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
 * <!-- $Id: PerformanceTest.java,v 1.4 2002-11-30 22:51:45 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.4 $
 */
public class PerformanceTest extends TestCase
{
    public void testPerformanceAndMemory() throws IllegalAccessException, InstantiationException
    {
        // these are exceptionally high due to Clover...
        double maxMemoryPerInvocation = 1.6;
        double timesSlowerTolerance = 41;
        double maxTimePerInvocation = 0.012;

        int numberOfInvocations = 100000;

        Intf intf = new Impl();

        ///CLOVER:OFF
        System.gc();
        long startMemory = Runtime.getRuntime().freeMemory();
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numberOfInvocations; i++)
        {
            intf.call();
        }

        long ordinaryTime = System.currentTimeMillis() - startTime;
        long ordinaryMemory = startMemory - Runtime.getRuntime().freeMemory();
        ///CLOVER:ON

        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(Intf.class);
        aspectClass.addInterceptor(NullInterceptor.class);
        aspectClass.addInterceptor(NullInterceptor.class);
        aspectClass.setTarget(Impl.class);

        intf = (Intf) aspectClass.newInstance();

        ///CLOVER:OFF
        System.gc();
        startMemory = Runtime.getRuntime().freeMemory();
        startTime = System.currentTimeMillis();

        for (int i = 0; i < numberOfInvocations; i++)
        {
            intf.call();
        }

        long aspectsTime = System.currentTimeMillis() - startTime;
        long aspectsMemory = startMemory - Runtime.getRuntime().freeMemory();
        ///CLOVER:ON

        double timesSlower = aspectsTime/ (double) ordinaryTime;
        double timesMoreMemory = aspectsMemory / (double) ordinaryMemory;

        double timePerInvocation = aspectsTime / (double) numberOfInvocations;
        double memoryPerInvocation = aspectsMemory / (double) numberOfInvocations;

        System.out.println();
        System.out.println("timesSlower = " + timesSlower);
        System.out.println("timesMoreMemory = " + timesMoreMemory);
        System.out.println("ordinaryTime = " + ordinaryTime);
        System.out.println("ordinaryMemory = " + ordinaryMemory);
        System.out.println("aspectsTime = " + aspectsTime);
        System.out.println("aspectsMemory = " + aspectsMemory);
        System.out.println("memoryPerInvocation = " + memoryPerInvocation);
        System.out.println("timePerInvocation = " + timePerInvocation);

        assertTrue("memory per invocation exceeded", memoryPerInvocation < maxMemoryPerInvocation);
        assertTrue("time per invocation exceeded", timePerInvocation < maxTimePerInvocation);
        assertTrue("time per invocation exceeded", timesSlowerTolerance > timesSlower);
    }

    public void testInstantiation() throws IllegalAccessException, InstantiationException
    {
        int timesBiggerTolerance = 69;
        int timesSlowerTolerance = 6;

        int numberOfInstances = 1000;

        // allocate a set of ordinary instances and check for footprint
        Impl[] impls = new Impl[numberOfInstances];

        ///CLOVER:OFF
        System.gc();
        long startMemory = Runtime.getRuntime().freeMemory();
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numberOfInstances; i++)
        {
            impls[i] = new Impl();
        }

        System.gc();
        long ordinaryTime = System.currentTimeMillis() - startTime;
        long ordinaryMemory = startMemory - Runtime.getRuntime().freeMemory();
        ///CLOVER:ON

        double memoryPerOrdinaryInstance = ordinaryMemory / (double) numberOfInstances;

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
        startTime = System.currentTimeMillis();

        Object[] objects = new Object[numberOfInstances];
        for (int i = 0; i < numberOfInstances; i++)
        {
            objects[i] = aspectClass.newInstance();
        }

        System.gc();
        long aspectsTime = System.currentTimeMillis() - startTime;
        long memory = startMemory - Runtime.getRuntime().freeMemory();
        ///CLOVER:ON

        double memoryPerInstance = memory / (double) numberOfInstances;

        double timesBigger = memory / (double) ordinaryMemory;
        double timesSlower = aspectsTime / (double) ordinaryTime;

        System.out.println();
        System.out.println("ordinaryTime = " + ordinaryTime);
        System.out.println("aspectsTime = " + aspectsTime);
        System.out.println("times slower = " + timesSlower);

        System.out.println("memory = " + memory);
        System.out.println("memoryPerOrdinaryInstance = " + memoryPerOrdinaryInstance);
        System.out.println("maxMemoryPerInstance = " + maxMemoryPerInstance);
        System.out.println("times bigger = " + timesBigger);
        System.out.println("memoryPerInstance = " + memoryPerInstance);

        assertTrue("memory per instance exceeded", timesBiggerTolerance > timesBigger);
        assertTrue("time per instantiation exceeded", timesSlowerTolerance > timesSlower);
    }
}
