package org.codehaus.nanning.cache;

import org.codehaus.nanning.cache.CacheTestCalculations;

public class CacheTestCalculationsImpl implements CacheTestCalculations {
    public double someHeavyCalculation(double input) {
        return 42.4711 * input;
    }
}
