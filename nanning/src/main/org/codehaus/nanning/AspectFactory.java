/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 * (C) 2003 Jon Tirsen
 */
package org.codehaus.nanning;

public interface AspectFactory {
    
    Object newInstance(Class classIdentifier);

    void reinitialize(AspectInstance aspectInstance);
}
