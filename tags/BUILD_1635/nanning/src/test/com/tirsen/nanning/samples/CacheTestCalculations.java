/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 * (C) 2003 Jon Tirsen
 */
package com.tirsen.nanning.samples;

public interface CacheTestCalculations {
    /**
     * @cache
     */
    public double someHeavyCalculation(double input);
}
