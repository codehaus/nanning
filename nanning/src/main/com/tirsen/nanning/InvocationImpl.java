/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.lang.reflect.Method;

/**
 * TODO document InvocationImpl
 *
 * <!-- $Id: InvocationImpl.java,v 1.2 2002-10-22 18:56:25 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
 */
class InvocationImpl implements Invocation
{
    private int index = -1;
    private Method method;
    private Object[] args;
    private Interceptor[] interceptors;
    private Object target;
    private Object proxy;

    public InvocationImpl(Object proxy, Method method, Object[] args, Interceptor[] interceptors, Object target)
    {
        this.proxy = proxy;
        this.method = method;
        this.args = args;
        this.interceptors = interceptors;
        this.target = target;
    }

    public Object invokeNext() throws Throwable
    {
        index++;
        if (interceptors != null && index < interceptors.length)
        {
            return interceptors[index].invoke(this);
        }
        else
        {
            return method.invoke(target, args);
        }
    }

    public Object getTarget()
    {
        return target;
    }

    public Object getProxy()
    {
        return proxy;
    }

    public int getCurrentIndex()
    {
        return index;
    }

    public int getNumberOfInterceptors()
    {
        return interceptors.length;
    }

    public Interceptor getInterceptor(int index)
    {
        return interceptors[index];
    }

    public Method getMethod()
    {
        return method;
    }

    public Object[] getArgs()
    {
        return args;
    }
}
