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
import java.util.HashMap;
import java.util.List;

/**
 * TODO document AspectInstance
 *
 * <!-- $Id: AspectInstance.java,v 1.4 2002-10-27 12:13:18 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.4 $
 */
class AspectInstance implements InvocationHandler
{
    class InvocationImpl implements Invocation
    {
        private int index = -1;
        private Method method;
        private Object[] args;
        private SideAspectInstance interfaceInstance;

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
            if(index < interceptors.length)
            {
                return interceptors[index].invoke(this);
            }
            else
            {
                return method.invoke(interfaceInstance.getTarget(), args);
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
    private SideAspectInstance[] interfaceInstances;
    private HashMap interfacesToInstancesIndex;

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
        SideAspectInstance interfaceInstance = (SideAspectInstance) interfacesToInstancesIndex.get(interfaceClass);
        return interfaceInstance.getTarget();
    }

    public Interceptor[] getProxyInterceptors()
    {
        // the actual class-specific interface-instance is at the first position
        return interfaceInstances[0].getInterceptors();
    }

    public Interceptor[] getInterceptors(Class interfaceClass)
    {
        SideAspectInstance interfaceInstance = (SideAspectInstance) interfacesToInstancesIndex.get(interfaceClass);
        return interfaceInstance.getInterceptors();
    }
}
