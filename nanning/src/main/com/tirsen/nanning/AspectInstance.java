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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * TODO document AspectInstance
 *
 * <!-- $Id: AspectInstance.java,v 1.8 2002-10-31 16:38:56 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.8 $
 */
class AspectInstance implements InvocationHandler
{
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
    private final SideAspectInstance[] interfaceInstances;
    private final HashMap interfacesToInstancesIndex;

    public AspectInstance(SideAspectInstance[] interfaceInstances)
    {
        this.interfaceInstances = interfaceInstances;
        // index this up for faster invocations
        interfacesToInstancesIndex = new HashMap();
        for (int i = 0; i < interfaceInstances.length; i++)
        {
            SideAspectInstance interfaceInstance = interfaceInstances[i];
            interfacesToInstancesIndex.put(interfaceInstance.getInterfaceClass(), interfaceInstance);
        }
    }

    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable
    {
        Class interfaceClass = method.getDeclaringClass();
        SideAspectInstance interfaceInstance = (SideAspectInstance) interfacesToInstancesIndex.get(interfaceClass);

        Invocation invocation = new InvocationImpl(method, args, interfaceInstance);
        return invocation.invokeNext();
    }

    Object createProxy()
    {
        List interfaces = new ArrayList(interfaceInstances.length);
        for (int i = 0; i < interfaceInstances.length; i++)
        {
            SideAspectInstance interfaceInstance = interfaceInstances[i];
            interfaces.add(interfaceInstance.getInterfaceClass());
        }

        return proxy = Proxy.newProxyInstance(getClass().getClassLoader(),
                (Class[]) interfaces.toArray(new Class[0]),
                this);
    }

    Object getTarget(Class interfaceClass)
    {
        SideAspectInstance interfaceInstance = getSideAspectInstance(interfaceClass);
        return interfaceInstance.getTarget();
    }

    Interceptor[] getProxyInterceptors()
    {
        // the actual class-specific interface-instance is at the first position
        return interfaceInstances[0].getInterceptors();
    }

    Interceptor[] getInterceptors(Class interfaceClass)
    {
        SideAspectInstance interfaceInstance = getSideAspectInstance(interfaceClass);
        return interfaceInstance.getInterceptors();
    }

    private SideAspectInstance getSideAspectInstance(Class interfaceClass)
    {
        return (SideAspectInstance) interfacesToInstancesIndex.get(interfaceClass);
    }

    public void setTarget(Class interfaceClass, Object target)
    {
        SideAspectInstance sideAspectInstance = getSideAspectInstance(interfaceClass);
        sideAspectInstance.setTarget(target);
    }
}
