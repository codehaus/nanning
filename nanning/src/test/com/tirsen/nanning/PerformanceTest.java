/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import junit.framework.TestCase;

/**
 * TODO document PerformanceTest
 *
 * <!-- $Id: PerformanceTest.java,v 1.7 2002-12-03 14:10:23 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.7 $
 */
public class PerformanceTest extends TestCase
{
    public static class StopWatch {
        ///CLOVER:OFF

        private long startMemory;
        private long startTime;
        private long time;
        private long memory;

        public StopWatch() {
            this(true);
        }

        public StopWatch(boolean doGC) {
            if (doGC) {
                System.gc(); System.gc(); System.gc();
            }
            startMemory = Runtime.getRuntime().freeMemory();
            startTime = System.currentTimeMillis();
        }

        public void stop() {
            time = System.currentTimeMillis() - startTime;
            memory = startMemory - Runtime.getRuntime().freeMemory();
        }

        public double getTimeSpent() {
            return time;
        }

        public double getMemoryUsed() {
            return memory;
        }

        public double getTimeSpent(int numberOfIterations) {
            return getTimeSpent() / (double) numberOfIterations;
        }

        public double getMemoryUsed(int numberOfIterations) {
            return getMemoryUsed() / (double) numberOfIterations;
        }
        ///CLOVER:ON
    }

    public void testPerformanceAndMemory() throws IllegalAccessException, InstantiationException
    {
        // these are exceptionally high due to Clover...
        double maxMemoryPerInvocation = 1.6;
        double timesSlowerTolerance = 48;
        double maxTimePerInvocation = 0.012;

        int numberOfInvocations = 100000;

        Intf intf = new Impl();

        ///CLOVER:OFF
        StopWatch ordinary = new StopWatch();

        for (int i = 0; i < numberOfInvocations; i++)
        {
            intf.call();
        }

        ordinary.stop();
        ///CLOVER:ON

        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(Intf.class);
        aspectClass.addInterceptor(NullInterceptor.class);
        aspectClass.addInterceptor(NullInterceptor.class);
        aspectClass.setTarget(Impl.class);

        intf = (Intf) aspectClass.newInstance();

        ///CLOVER:OFF
        StopWatch aspect = new StopWatch();

        for (int i = 0; i < numberOfInvocations; i++)
        {
            intf.call();
        }

        aspect.stop();
        ///CLOVER:ON

        double timesSlower = aspect.getTimeSpent() / ordinary.getTimeSpent();
        double timesMoreMemory = aspect.getMemoryUsed() / ordinary.getMemoryUsed();

        System.out.println();
        System.out.println("timesSlower = " + timesSlower);
        System.out.println("timesMoreMemory = " + timesMoreMemory);
        System.out.println("ordinaryTime = " + ordinary.getTimeSpent());
        System.out.println("ordinaryMemory = " + ordinary.getMemoryUsed());
        System.out.println("aspectsTime = " + aspect.getTimeSpent());
        System.out.println("aspectsMemory = " + aspect.getMemoryUsed());
        System.out.println("memoryPerInvocation = " + aspect.getMemoryUsed(numberOfInvocations));
        System.out.println("timePerInvocation = " + aspect.getTimeSpent(numberOfInvocations));

        assertTrue("memory per invocation exceeded", aspect.getMemoryUsed(numberOfInvocations) < maxMemoryPerInvocation);
        assertTrue("time per invocation exceeded", aspect.getTimeSpent(numberOfInvocations) < maxTimePerInvocation);
        assertTrue("time per invocation exceeded", timesSlowerTolerance > timesSlower);
    }

    public void testInstantiation() throws IllegalAccessException, InstantiationException
    {
        int timesBiggerTolerance = 69;
        int timesSlowerTolerance = 866;

        int numberOfInstances = 10000;

        // allocate a set of ordinary instances and check for footprint
        Object[] objects = new Impl[numberOfInstances];

        ///CLOVER:OFF
        StopWatch ordinary = new StopWatch();

        for (int i = 0; i < numberOfInstances; i++)
        {
            objects[i] = new Impl();
        }

        ordinary.stop();
        ///CLOVER:ON

        // determine max-memory per aspect-instance compared to ordinary instances
        double maxMemoryPerInstance = ordinary.getMemoryUsed(numberOfInstances) * timesBiggerTolerance;

        // setup a factory
        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(Intf.class);
        aspectClass.addInterceptor(NullInterceptor.class);
        aspectClass.addInterceptor(NullInterceptor.class);
        aspectClass.setTarget(Impl.class);

        // instantiates a bunch of aspect-instances
        ///CLOVER:OFF
        StopWatch aspect = new StopWatch();

        Object[] aspects = new Object[numberOfInstances];
        for (int i = 0; i < numberOfInstances; i++)
        {
            aspects[i] = aspectClass.newInstance();
        }

        aspect.stop();
        ///CLOVER:ON

        // do something with the objects after the gc so the compiler doesn't optimize them away
        for (int i = 0; i < objects.length; i++) {
            Object object = objects[i];
        }

        double timesBigger = aspect.getMemoryUsed() / ordinary.getMemoryUsed();
        double timesSlower = aspect.getTimeSpent() / ordinary.getTimeSpent();

        System.out.println();
        System.out.println("ordinaryTime = " + ordinary.getTimeSpent());
        System.out.println("aspectsTime = " + aspect.getTimeSpent());
        System.out.println("times slower = " + timesSlower);

        System.out.println("memory = " + aspect.getMemoryUsed());
        System.out.println("memoryPerOrdinaryInstance = " + ordinary.getMemoryUsed(numberOfInstances));
        System.out.println("maxMemoryPerInstance = " + maxMemoryPerInstance);
        System.out.println("times bigger = " + timesBigger);
        System.out.println("memoryPerInstance = " + aspect.getMemoryUsed(numberOfInstances));

        assertTrue("memory per instance exceeded", timesBiggerTolerance > timesBigger);
        assertTrue("time per instantiation exceeded", timesSlowerTolerance > timesSlower);
    }
}
