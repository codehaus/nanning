package com.tirsen.nanning;

import com.tirsen.nanning.attribute.AbstractAttributesTest;
import com.tirsen.nanning.definition.AspectClass;
import com.tirsen.nanning.definition.InterceptorDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO document AttributesTest
 *
 * <!-- $Id: AttributeFilterTest.java,v 1.4 2003-01-24 13:29:30 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.4 $
 */
public class AttributeFilterTest  extends AbstractAttributesTest {
    private static List expectedMethods;

    protected void setUp() throws Exception {
        super.setUp();

        // initialize the lists
        expectedMethods = new ArrayList();
        AttributeFilterInterceptor.invokedMethods.clear();
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
