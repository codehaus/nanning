/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;


/**
 * Intercepts calls on an interface on it's way to the target, these are nested "on top" of the target.
 *
 * <!-- $Id: Interceptor.java,v 1.3 2002-10-30 20:10:54 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.3 $
 */
public interface Interceptor
{
    /**
     * Do the stuff you want to do before and after the invocation. Polite implementations would certainly like
     * to implement {@link Invocation#invokeNext()}.
     * @param invocation
     * @return the result of the call to {@link Invocation#invokeNext()}, might be intercepted by the interceptor.
     * @throws Throwable if the interceptors or the target-object throws an exception.
     */
    Object invoke(Invocation invocation) throws Throwable;
}
