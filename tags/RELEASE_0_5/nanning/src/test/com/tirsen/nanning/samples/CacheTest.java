/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 * (C) 2003 Jon Tirsen
 */
package com.tirsen.nanning.samples;

import com.tirsen.nanning.config.AspectSystem;
import com.tirsen.nanning.config.FindTargetMixinAspect;
import com.tirsen.nanning.attribute.AbstractAttributesTest;
import com.tirsen.nanning.Aspects;

public class CacheTest extends AbstractAttributesTest {

    public void testCacheCounter() {
        AspectSystem aspectSystem = new AspectSystem();
        aspectSystem.addAspect(new FindTargetMixinAspect());
        CacheCounterAspect counter = new CacheCounterAspect(new CacheInterceptor());
        aspectSystem.addAspect(counter);

        assertEquals(1, counter.getCacheHitRatio(), 0.01);
        CacheTestCalculations cacheTestCalculations =
                (CacheTestCalculations) aspectSystem.newInstance(CacheTestCalculations.class);
        assertEquals(42.4711, cacheTestCalculations.someHeavyCalculation(1), 0.01);
        assertEquals(1, counter.totalCount);
        assertEquals(1, counter.missCount);
        assertEquals(0, counter.getCacheHitRatio(), 0.01);

        assertEquals(42.4711, cacheTestCalculations.someHeavyCalculation(1), 0.01);
        assertEquals(2, counter.totalCount);
        assertEquals(1, counter.missCount);
        assertEquals(1, counter.totalCount - counter.missCount);
        assertEquals(0.5, (counter.totalCount - counter.missCount) / (double) counter.totalCount, 0.01);
        assertEquals(0.5, counter.getCacheHitRatio(), 0.01);

        assertEquals(42.4711, cacheTestCalculations.someHeavyCalculation(1), 0.01);
        assertEquals(0.66, counter.getCacheHitRatio(), 0.01);

        assertEquals(84.9422, cacheTestCalculations.someHeavyCalculation(2), 0.01);
        assertEquals(0.5, counter.getCacheHitRatio(), 0.01);

        assertEquals(127.4132, cacheTestCalculations.someHeavyCalculation(3), 0.01);
        assertEquals(0.4, counter.getCacheHitRatio(), 0.01);

        assertEquals(3, counter.missCount);
        cacheTestCalculations.someHeavyCalculation(1);
        assertEquals(3, counter.missCount);
        ((CacheInterceptor)
                Aspects.findFirstInterceptorWithClass(cacheTestCalculations, CacheInterceptor.class)).clearCache();
        cacheTestCalculations.someHeavyCalculation(1);
        assertEquals(4, counter.missCount);
    }

}
