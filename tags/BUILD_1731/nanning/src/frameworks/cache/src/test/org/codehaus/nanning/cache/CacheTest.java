/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 * (C) 2003 Jon Tirsen
 */
package org.codehaus.nanning.cache;

import org.codehaus.nanning.config.AspectSystem;
import org.codehaus.nanning.config.FindTargetMixinAspect;
import org.codehaus.nanning.attribute.AbstractAttributesTest;
import org.codehaus.nanning.Aspects;
import org.codehaus.nanning.cache.CacheAspect;
import org.codehaus.nanning.cache.CacheInterceptor;

public class CacheTest extends AbstractAttributesTest {

    public void testCacheCounter() {
        AspectSystem aspectSystem = new AspectSystem();
        aspectSystem.addAspect(new FindTargetMixinAspect());
        CacheAspect counter = new CacheAspect(CacheInterceptor.class);
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
