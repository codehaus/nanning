package com.tirsen.nanning;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO document AttributesTest
 *
 * <!-- $Id: AttributeFilterTest.java,v 1.1 2003-01-06 14:32:27 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public class AttributeFilterTest extends TestCase {
    private static List expectedMethods;

    protected void setUp() throws Exception {
        super.setUp();

        // initialize the lists
        expectedMethods = new ArrayList();
        AttributeFilterInterceptor.invokedMethods.clear();

        // compile the Attributes they haven't been already
        AttributesTest.compileAttributes();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAttributes() throws Exception {
        // expected methods
        expectedMethods.add("doItAgain");
        expectedMethods.add("oupsIDidItAgain");
        //create the aspect and run the methods
        SomeAspect aspect = (SomeAspect) createAspectProxy();
        aspect.doIt();
        aspect.doItAgain();
        aspect.oupsIDidItAgain();

        verify();
    }

    private void verify() {
        assertEquals("number of executed methods was not as expected", expectedMethods.size(), AttributeFilterInterceptor.invokedMethods.size());
        for (int i = 0; i < expectedMethods.size(); i++) {
            String methodName = (String) expectedMethods.get(i);
            assertTrue(methodName + " was not invoked", AttributeFilterInterceptor.invokedMethods.contains(methodName));
        }
    }

    private Object createAspectProxy() {
        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(SomeAspect.class);
        InterceptorDefinition interceptorDefinition = new InterceptorDefinition(AttributeFilterInterceptor.class);
        aspectClass.addInterceptor(interceptorDefinition);
        aspectClass.setTarget(SomeAspectImpl.class);

        return aspectClass.newInstance();

    }


    public static class SomeAspectImpl implements SomeAspect {
        public boolean doIt() {
            return true;
        }


        public boolean doItAgain() {
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
