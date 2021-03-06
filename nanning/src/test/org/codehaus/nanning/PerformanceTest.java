/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.codehaus.nanning;

import org.codehaus.nanning.attribute.AbstractAttributesTest;
import org.codehaus.nanning.attribute.Attributes;
import org.codehaus.nanning.attribute.AttributesTestClass;
import org.codehaus.nanning.samples.StopWatch;

import java.lang.reflect.Method;

/**
 * TODO document PerformanceTest
 *
 * <!-- $Id: PerformanceTest.java,v 1.3 2003-09-11 11:25:37 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.3 $
 */
public class PerformanceTest extends AbstractAttributesTest {
    public void testInvocation() throws IllegalAccessException, InstantiationException {
        // these are exceptionally high due to Clover...
        double maxMemoryPerInvocation = 15;
        double timesSlowerTolerance = 4;
        double maxTimePerInvocation = 0.014;

        int numberOfInvocations = 100000;

        Intf intf = new IntfImpl();

        ///CLOVER:OFF
        StopWatch ordinary = new StopWatch();

        for (int i = 0; i < numberOfInvocations; i++) {
            intf.call();
        }

        ordinary.stop();
        ///CLOVER:ON

        AspectInstance instance = new AspectInstance();
        Mixin mixin = new Mixin(Intf.class, new IntfImpl());
        mixin.addInterceptor(new NullInterceptor());
        mixin.addInterceptor(new NullInterceptor());
        instance.addMixin(mixin);

        intf = (Intf) instance.getProxy();

        ///CLOVER:OFF
        StopWatch aspect = new StopWatch();

        for (int i = 0; i < numberOfInvocations; i++) {
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

        assertLowerThan("memory per invocation exceeded", aspect.getMemoryUsed(numberOfInvocations), maxMemoryPerInvocation);
        assertLowerThan("time per invocation exceeded", aspect.getTimeSpent(numberOfInvocations), maxTimePerInvocation);
        assertLowerThan("time per invocation exceeded", timesSlower, timesSlowerTolerance);
    }

    public void testAttributes() throws NoSuchMethodException {
        long maxTime = 63;
        long maxMemory = 0;

        // let the cache do it's thang
        Attributes.getAttribute(AttributesTestClass.class, "class.attribute");
        Method method = AttributesTestClass.class.getMethod("method", null);
        StopWatch stopWatch = new StopWatch(true);

        for (int i = 0; i < 1000; i++) {
            assertEquals("classValue", Attributes.getAttribute(AttributesTestClass.class, "class.attribute"));
            assertEquals("methodValue", Attributes.getAttribute(method, "method.attribute"));
        }

        stopWatch.stop();
        System.out.println();
        System.out.println("time spent " + stopWatch.getTimeSpent());
        System.out.println("memory used " + stopWatch.getMemoryUsed());
        assertLowerThan("time exceeded", stopWatch.getTimeSpent(), maxTime);
        assertLowerThan("memory exceeded", stopWatch.getMemoryUsed(), maxMemory);
    }

    private void assertLowerThan(String reason, double observed, double maximum) {
        assertTrue(reason + ", " + observed + " > " + maximum, observed <= maximum);
    }

    public void testInheritedAttributes() throws NoSuchMethodException {
        long maxTime = 60;
        long maxMemory = 180000;

        // let the cache do it's thang
        Attributes.getInheritedAttribute(AttributesTestClass.class, "class.attribute");
        Method method = AttributesTestClass.class.getMethod("method", null);
        StopWatch stopWatch = new StopWatch(true);

        for (int i = 0; i < 1000; i++) {
            assertEquals("classValue", Attributes.getInheritedAttribute(AttributesTestClass.class, "class.attribute"));
            assertEquals("methodValue", Attributes.getAttribute(method, "method.attribute"));
        }

        stopWatch.stop();
        System.out.println();
        System.out.println("time spent " + stopWatch.getTimeSpent());
        System.out.println("memory used " + stopWatch.getMemoryUsed());
        assertLowerThan("time exceeded", stopWatch.getTimeSpent(), maxTime);
        assertLowerThan("memory exceeded", stopWatch.getMemoryUsed(), maxMemory);
    }
}
