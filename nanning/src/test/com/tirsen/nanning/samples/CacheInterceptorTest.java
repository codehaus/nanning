package com.tirsen.nanning.samples;

import com.tirsen.nanning.attribute.AbstractAttributesTest;
import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MixinInstance;

import java.lang.reflect.Method;

public class CacheInterceptorTest extends AbstractAttributesTest {
    private Method someHeavyCalculationMethod;
    private CacheInterceptor cacheInterceptor;
    private boolean wasCalled;

    protected void setUp() throws Exception {
        super.setUp();

        cacheInterceptor = new CacheInterceptor();
        someHeavyCalculationMethod =
                CacheTestCalculations.class.getMethod("someHeavyCalculation",
                        new Class[] { double.class } );
    }

    public void testIntercept() {
        AspectInstance aspectInstance = new AspectInstance();
        MixinInstance mixin = new MixinInstance(CacheTestCalculations.class, null);
        mixin.addInterceptor(someHeavyCalculationMethod, cacheInterceptor);
        aspectInstance.addMixin(mixin);
        mixin.setTarget(new CacheTestCalculations() {
            public double someHeavyCalculation(double input) {
                wasCalled = true;
                return input;
            }
        });
        CacheTestCalculations cacheTestCalculations = (CacheTestCalculations) aspectInstance.getProxy();

        // non-cached call
        assertFalse(wasCalled);
        assertEquals(0, cacheTestCalculations.someHeavyCalculation(0), 0);
        assertTrue(wasCalled);

        // cached call
        wasCalled = false;
        assertEquals(0, cacheTestCalculations.someHeavyCalculation(0), 0);
        assertFalse(wasCalled);

        // flush cache and non-cached call
        cacheInterceptor.clearCache();
        wasCalled = false;
        assertEquals(0, cacheTestCalculations.someHeavyCalculation(0), 0);
        assertTrue(wasCalled);
    }

    public void testFilterMethod() throws NoSuchMethodException {
        assertTrue(cacheInterceptor.interceptsMethod(
                null, null,
                someHeavyCalculationMethod));
        assertFalse(cacheInterceptor.interceptsMethod(
                null, null,
                Object.class.getMethod("toString", null)));
    }
}
