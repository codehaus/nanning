/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 * (C) 2003 Jon Tirsen
 */
package com.tirsen.nanning;

public interface AspectFactory {
    Object newInstance(Class classIdentifier);

    Object newInstance(Class classIdentifier, Object[] targets);
}
