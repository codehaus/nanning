/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.lang.reflect.Method;


/**
 * Description of the current invocation, given to an interceptor upon method-call.
 *
 * <!-- $Id: Invocation.java,v 1.3 2002-10-22 18:56:25 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.3 $
 */
public interface Invocation
{
    /**
     * Invoke the next interceptor in the stack (or the target if you're last).
     *
     * @return the result of the call on the target (might be intercepted and changed by an interceptor).
     * @throws Throwable if the call throws an exception.
     */
    Object invokeNext() throws Throwable;

    /**
     * Gets the target of the call.
     *
     * @return the actual target object.
     */
    Object getTarget();

    /**
     * Gets the aspected object the call is part of. (TODO might need to change name...)
     *
     * @return the aspected object.
     */
    Object getProxy();

    /**
     * Gets the index of the current interceptor.
     *
     * @return the index of the current interceptor.
     */
    int getCurrentIndex();

    /**
     * Gets the total number of interceptors in the chain.
     *
     * @return the total number of interceptors in the chain.
     */
    int getNumberOfInterceptors();

    /**
     * Gets the interceptors at the specified index.
     *
     * @param index index to the interceptor to retrieve.
     * @return the interceptors at the specified index.
     */
    Interceptor getInterceptor(int index);

    /**
     * Gets the method being called.
     *
     * @return the method being called.
     */
    Method getMethod();

    /**
     * Gets the arguments to the call.
     *
     * @return the arguments to the call.
     */
    Object[] getArgs();
}
