package org.codehaus.nanning.samples;

public class CacheTestCalculationsImpl implements CacheTestCalculations {
    public double someHeavyCalculation(double input) {
        return 42.4711 * input;
    }
}
