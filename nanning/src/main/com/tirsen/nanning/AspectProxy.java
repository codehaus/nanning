/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * TODO document AspectProxy
 *
 * <!-- $Id: AspectProxy.java,v 1.1.1.1 2002-10-20 09:33:53 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1.1.1 $
 */
public class AspectProxy
{
    private Class interfaceClass;
    private Object realObject;
    private Aspect[] aspects = new Aspect[0];
    private Object proxy;

    public AspectProxy(Object implementation)
    {
        this.realObject = implementation;
    }

    public static AspectProxy create(Object implementation)
    {
        AspectProxy aspectProxy = new AspectProxy(implementation);
        return aspectProxy;
    }

    private class MyInvoicationHandler implements InvocationHandler
    {
        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable
        {
            AspectContext aspectChain = new AspectChainImpl();
            return aspectChain.invokeNext(method, args, aspectChain);
        }
    }

    private class AspectChainImpl implements AspectContext
    {
        private int index = 0;

        public Object invokeNext(Method method, Object[] args, AspectContext aspectChain) throws Throwable
        {
            if(index < aspects.length)
            {
                return aspects[index++].invoke(method, args, aspectChain);
            }
            else
            {
                return method.invoke(realObject, args);
            }
        }

        public Object getRealObject()
        {
            return realObject;
        }

        public Object getProxy()
        {
            return AspectProxy.this.getProxy();
        }
    }

    public void setInterfaceClass(Class interfaceClass)
    {
        this.interfaceClass = interfaceClass;
    }

    public Object getProxy()
    {
        if (proxy == null)
        {
            proxy = createProxy();
        }
        return proxy;
    }

    private Object createProxy()
    {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(),
                        new Class[] { interfaceClass },
                        new MyInvoicationHandler());
    }

    public void addAspect(Aspect aspect)
    {
        List aspectsList = new ArrayList(aspects.length + 1);
        aspectsList.addAll(Arrays.asList(aspects));
        aspectsList.add(aspect);
        aspects = (Aspect[]) aspectsList.toArray(new Aspect[0]);
    }
}
