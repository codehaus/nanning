/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import junit.framework.TestCase;
import com.tirsen.nanning.samples.StopWatch;
import com.tirsen.nanning.definition.AspectClass;
import com.tirsen.nanning.attribute.Attributes;

/**
 * TODO document PerformanceTest
 *
 * <!-- $Id: PerformanceTest.java,v 1.11 2003-01-19 12:09:04 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.11 $
 */
public class PerformanceTest extends TestCase
{
    public void testInvocation() throws IllegalAccessException, InstantiationException
    {
        // these are exceptionally high due to Clover...
        double maxMemoryPerInvocation = 1.6;
        double timesSlowerTolerance = 52;
        double maxTimePerInvocation = 0.007;

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
//        System.out.println("ordinaryTime = " + ordinary.getTimeSpent());
//        System.out.println("ordinaryMemory = " + ordinary.getMemoryUsed());
//        System.out.println("aspectsTime = " + aspect.getTimeSpent());
//        System.out.println("aspectsMemory = " + aspect.getMemoryUsed());
//        System.out.println("memoryPerInvocation = " + aspect.getMemoryUsed(numberOfInvocations));
        System.out.println("timePerInvocation = " + aspect.getTimeSpent(numberOfInvocations));

        assertTrue("memory per invocation exceeded", aspect.getMemoryUsed(numberOfInvocations) < maxMemoryPerInvocation);
        assertTrue("time per invocation exceeded", aspect.getTimeSpent(numberOfInvocations) < maxTimePerInvocation);
        assertTrue("time per invocation exceeded", timesSlowerTolerance > timesSlower);
    }

    public void testInstantiation() throws IllegalAccessException, InstantiationException
    {
        int timesBiggerTolerance = 69;
//        int timesSlowerTolerance = 866;

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
//        System.out.println("ordinaryTime = " + ordinary.getTimeSpent());
//        System.out.println("aspectsTime = " + aspect.getTimeSpent());
        System.out.println("times slower = " + timesSlower);

//        System.out.println("memory = " + aspect.getMemoryUsed());
//        System.out.println("memoryPerOrdinaryInstance = " + ordinary.getMemoryUsed(numberOfInstances));
//        System.out.println("maxMemoryPerInstance = " + maxMemoryPerInstance);
//        System.out.println("memoryPerInstance = " + aspect.getMemoryUsed(numberOfInstances));
        System.out.println("times bigger = " + timesBigger);

        assertTrue("memory per instance exceeded", timesBiggerTolerance > timesBigger);
//        assertTrue("time per instantiation exceeded", timesSlowerTolerance > timesSlower);
    }

    public void testAttributes() {
        long maxTime = 2672;
        long maxMemory = 355753;

        // let the cache do it's thang
        Attributes.getAttribute(AttributesTestClass.class, "classAttribute");
        StopWatch stopWatch = new StopWatch(true);

        for (int i = 0; i < 1000; i++) {
            Attributes.getAttribute(AttributesTestClass.class, "classAttribute");
        }

        stopWatch.stop();
        System.out.println();
        System.out.println("time spent " + stopWatch.getTimeSpent());
        System.out.println("memory used " + stopWatch.getMemoryUsed());
        assertTrue("time exceeded", stopWatch.getTimeSpent() < maxTime);
        assertTrue("memory exceeded", stopWatch.getMemoryUsed() < maxMemory);
    }

    public void testInheritedAttributes() {
        long maxTime = 4454;
        long maxMemory = 369409;

        // let the cache do it's thang
        Attributes.getInheritedAttribute(AttributesTestClass.class, "classAttribute");
        StopWatch stopWatch = new StopWatch(true);

        for (int i = 0; i < 1000; i++) {
            Attributes.getInheritedAttribute(AttributesTestClass.class, "classAttribute");
        }

        stopWatch.stop();
        System.out.println();
        System.out.println("time spent " + stopWatch.getTimeSpent());
        System.out.println("memory used " + stopWatch.getMemoryUsed());
        assertTrue("time exceeded", stopWatch.getTimeSpent() < maxTime);
        assertTrue("memory exceeded", stopWatch.getMemoryUsed() < maxMemory);
    }
}
