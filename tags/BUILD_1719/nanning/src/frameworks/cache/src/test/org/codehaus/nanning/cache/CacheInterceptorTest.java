package org.codehaus.nanning.cache;

import org.codehaus.nanning.attribute.AbstractAttributesTest;
import org.codehaus.nanning.AspectInstance;
import org.codehaus.nanning.MixinInstance;
import org.codehaus.nanning.cache.CacheInterceptor;

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
}
