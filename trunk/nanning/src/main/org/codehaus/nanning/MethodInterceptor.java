/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.codehaus.nanning;




/**
 * Intercepts calls on an interface on it's way to the target, these are nested "on top" of the target.
 *
 * <!-- $Id: MethodInterceptor.java,v 1.2 2003-07-16 13:05:20 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
 */
public interface MethodInterceptor extends Interceptor {
    /**
     * Do the stuff you want to do before and after the invocation. Polite implementations would certainly like
     * to implement {@link Invocation#invokeNext()}.
     * @param invocation
     * @return the result of the call to {@link Invocation#invokeNext()}, might be intercepted by the interceptor.
     * @throws Throwable if the interceptors or the target-object throws an exception.
     */
    Object invoke(Invocation invocation) throws Throwable;
}
