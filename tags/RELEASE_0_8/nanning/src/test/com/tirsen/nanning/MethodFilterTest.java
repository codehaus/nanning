/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.util.ArrayList;
import java.util.List;

import com.tirsen.nanning.definition.AspectClass;
import com.tirsen.nanning.definition.BasicInterceptor;
import com.tirsen.nanning.definition.InterceptorDefinition;
import junit.framework.TestCase;

/**
 * TODO document MethodFilterTest
 *
 * <!-- $Id: MethodFilterTest.java,v 1.7 2003-05-11 13:40:52 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.7 $
 */

public class MethodFilterTest extends TestCase {

    private static List invokedMethods;
    private static List expectedMethods;

    protected void setUp() throws Exception {
        invokedMethods = new ArrayList();
        expectedMethods = new ArrayList();
    }

    /**
     * creates a proxy object with a sample InterceptorDefinition which will have methodNameFilter
     * attribute set to parameter 'filter'
     * @param filter
     * @return
     */
    private Object createAspectProxy(String filter) {
        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(SomeAspect.class);
        InterceptorDefinition interceptorDefinition = new InterceptorDefinition(TestFilterMethodsInterceptor.class);
        interceptorDefinition.setAttribute("methodNameFilter", filter);
        aspectClass.addInterceptor(interceptorDefinition);
        aspectClass.setTarget(SomeAspectImpl.class);

        return aspectClass.newInstance();

    }


    public void testA() {
        Object proxy = createAspectProxy("doIt.*");
        expectedMethods.add("doIt");
        expectedMethods.add("doItAgain");
        SomeAspect aspect = (SomeAspect) proxy;

        aspect.doIt();
        aspect.doItAgain();
        aspect.oupsIDidItAgain();

        verify();
    }


    public void testB() {
        Object proxy = createAspectProxy(".*Again.*");
        expectedMethods.add("doItAgain");
        expectedMethods.add("oupsIDidItAgain");

        SomeAspect aspect = (SomeAspect) proxy;

        aspect.doIt();
        aspect.doItAgain();
        aspect.oupsIDidItAgain();

        verify();
    }

    public void testC() {
        Object proxy = createAspectProxy(".*");
        expectedMethods.add("doIt");
        expectedMethods.add("doItAgain");
        expectedMethods.add("oupsIDidItAgain");

        SomeAspect aspect = (SomeAspect) proxy;
        aspect.doIt();
        aspect.doItAgain();
        aspect.oupsIDidItAgain();

        verify();
    }

    public void testD() {
        Object proxy = createAspectProxy("noMethodLikeThat");

        SomeAspect aspect = (SomeAspect) proxy;
        aspect.doIt();
        aspect.doItAgain();
        aspect.oupsIDidItAgain();

        verify();
    }

    private void verify() {
        assertEquals("number of executed methods was not as expected",
                     expectedMethods.size(), invokedMethods.size());
        for (int i = 0; i < expectedMethods.size(); i++) {
            String methodName = (String) expectedMethods.get(i);
            assertTrue(methodName + " was not invoked", invokedMethods.contains(methodName));
        }
    }


    public static class TestFilterMethodsInterceptor extends BasicInterceptor {
        public Object invoke(Invocation invocation) throws Throwable {
            String methodName = invocation.getMethod().getName();
            //System.out.println ("method name: " + methodName);

            invokedMethods.add(methodName);
            return invocation.invokeNext();
        }

    }


    public static class SomeAspectImpl implements SomeAspect {
        public boolean doIt() {
            //System.out.println ("I am doing it");
            return true;
        }


        public boolean doItAgain() {
            //System.out.println ("I am doing it again");
            return true;
        }

        public boolean oupsIDidItAgain() {
            return true;
        }


    }

    public static interface SomeAspect {
        public boolean doIt();

        public boolean doItAgain();

        public boolean oupsIDidItAgain();

    }


}
