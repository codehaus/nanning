/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO document AspectInstance
 *
 * <!-- $Id: AspectInstance.java,v 1.11 2002-11-17 14:03:34 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.11 $
 */
class AspectInstance implements InvocationHandler
{
    static ThreadLocal currentThis = new ThreadLocal();

    class InvocationImpl implements Invocation
    {
        private int index = -1;
        private final Method method;
        private final Object[] args;
        private final SideAspectInstance interfaceInstance;

        public InvocationImpl(Method method, Object[] args, SideAspectInstance interfaceInstance)
        {
            this.method = method;
            this.args = args;
            this.interfaceInstance = interfaceInstance;
        }

        public Object invokeNext() throws Throwable
        {
            index++;
            Interceptor[] interceptors = interfaceInstance.getInterceptors();
            if (index < interceptors.length)
            {
                return interceptors[index].invoke(this);
            }
            else
            {
                try {
                    return method.invoke(interfaceInstance.getTarget(), args);
                } catch (InvocationTargetException e) {
                    throwRealException(e);
                    throw e;
                }
            }
        }

        private void throwRealException(InvocationTargetException e) throws Exception {
            Throwable realException = e.getTargetException();
            if (realException instanceof Error)
            {
                throw (Error) realException;
            }
            else if (realException instanceof RuntimeException)
            {
                throw (RuntimeException) realException;
            }
            else
            {
                throw (Exception) realException;
            }
        }

        public Interceptor getInterceptor(int index)
        {
            return interfaceInstance.getInterceptors()[index];
        }

        public Object getTarget()
        {
            return interfaceInstance.getTarget();
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
            return interfaceInstance.getInterceptors().length;
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

    private Object proxy;
    private final SideAspectInstance[] sideAspectInstances;
    private final AspectClass aspectClass;

    public AspectInstance(AspectClass aspectClass, SideAspectInstance[] sideAspectInstances) {
        this.proxy = proxy;
        this.sideAspectInstances = sideAspectInstances;
        this.aspectClass = aspectClass;
    }

    void setProxy(Object proxy) {
        this.proxy = proxy;
    }

    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable
    {
        Class interfaceClass = method.getDeclaringClass();
        if (interfaceClass != Object.class)
        {
            Object prevThis = currentThis.get();
            try
            {
                currentThis.set(proxy);
                SideAspectInstance interfaceInstance = getSideAspectInstance(interfaceClass);
                // if it wasn't defined by any of the specified interfaces let's assume it's the default one (ie. index 0)

                Invocation invocation = new InvocationImpl(method, args, interfaceInstance);
                return invocation.invokeNext();
            }
            finally
            {
                currentThis.set(prevThis);
            }
        }
        else
        {
            // I take care of all calls to Object (such as equals, toString and so on)
            return method.invoke(this, args);
        }
    }

    Object getTarget(Class interfaceClass)
    {
        SideAspectInstance interfaceInstance = getSideAspectInstance(interfaceClass);
        return interfaceInstance.getTarget();
    }

    Interceptor[] getProxyInterceptors()
    {
        // the actual class-specific interface-instance is at the first position
        return sideAspectInstances[0].getInterceptors();
    }

    Interceptor[] getInterceptors(Class interfaceClass)
    {
        SideAspectInstance interfaceInstance = getSideAspectInstance(interfaceClass);
        return interfaceInstance.getInterceptors();
    }

    private SideAspectInstance getSideAspectInstance(Class interfaceClass)
    {
        return sideAspectInstances[aspectClass.getSideAspectIndexForInterface(interfaceClass)];
    }

    public void setTarget(Class interfaceClass, Object target)
    {
        SideAspectInstance sideAspectInstance = getSideAspectInstance(interfaceClass);
        sideAspectInstance.setTarget(target);
    }

    public String toString()
    {
        SideAspectInstance defaultInterfaceInstance = sideAspectInstances[0];
        return new ToStringBuilder(this)
                .append("interface", defaultInterfaceInstance.getInterfaceClass().getName())
                .append("target", defaultInterfaceInstance.getTarget())
                .toString();
    }
}
