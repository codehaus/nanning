/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TODO document AspectProxy
 *
 * <!-- $Id: AspectProxy.java,v 1.2 2002-10-21 21:07:31 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
 */
public class AspectProxy implements InvocationHandler
{
    private Object target;
    private Interceptor[] aspects;
    private Object proxy;

    public AspectProxy(Object implementation)
    {
        this.target = implementation;
    }

    public static AspectProxy create(Object implementation)
    {
        AspectProxy aspectProxy = new AspectProxy(implementation);
        return aspectProxy;
    }

    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable
    {
        Invocation invocation = new InvocationImpl(method, args);
        return invocation.invokeNext(invocation);
    }

    private class InvocationImpl implements Invocation
    {
        private int index = -1;
        private Method method;
        private Object[] args;

        public InvocationImpl(Method method, Object[] args)
        {
            this.method = method;
            this.args = args;
        }

        public Object invokeNext(Invocation invocation) throws Throwable
        {
            index++;
            if (aspects != null && index < aspects.length)
            {
                return aspects[index].invoke(invocation);
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
            return AspectProxy.this.proxy;
        }

        public int getCurrentIndex()
        {
            return index;
        }

        public int getNumberOfAspects()
        {
            return aspects.length;
        }

        public Interceptor getAspect(int index)
        {
            return aspects[index];
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

    public Object createProxy(Class[] interfaceClasses)
    {
        // TODO: check that proxy is not already created
        return proxy =
                Proxy.newProxyInstance(getClass().getClassLoader(),
                        interfaceClasses,
                        this);
    }

    public void addAspect(Interceptor aspect)
    {
        List aspectsList = null;
        if (aspects != null)
        {
            aspectsList = new ArrayList(aspects.length + 1);
            aspectsList.addAll(Arrays.asList(aspects));
        }
        else
        {
            aspectsList = new ArrayList();
        }
        aspectsList.add(aspect);
        aspects = (Interceptor[]) aspectsList.toArray(new Interceptor[0]);
    }

    Interceptor[] getAspects()
    {
        return aspects;
    }

    public Object getTarget()
    {
        return target;
    }
}
