/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.codehaus.nanning;

import java.lang.reflect.Method;


/**
 * Intercepts calls on an interface on it's way to the target, these are nested "on top" of the target.
 *
 * <!-- $Id: MethodInterceptor.java,v 1.1 2003-07-04 10:53:59 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.1 $
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
