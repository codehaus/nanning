/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import org.apache.commons.collections.FastHashMap;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * TODO document AspectInstance
 *
 * <!-- $Id: AspectInstance.java,v 1.2 2002-10-22 18:56:25 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
 */
class AspectInstance implements InvocationHandler
{
    private Object proxy;
    private InterfaceInstance[] interfaceInstances;
    private AspectClass aspectClass;
    private FastHashMap interfacesToInstancesIndex;

    AspectInstance(AspectClass aspectClass, InterfaceInstance[] interfaceInstances)
    {
        this.aspectClass = aspectClass;
        this.interfaceInstances = interfaceInstances;

        // index this up for faster invocations
        interfacesToInstancesIndex = new FastHashMap();
        for (int i = 0; i < interfaceInstances.length; i++)
        {
            InterfaceInstance interfaceInstance = interfaceInstances[i];
            interfacesToInstancesIndex.put(interfaceInstance.getInterfaceClass(), interfaceInstance);
        }
        interfacesToInstancesIndex.setFast(true);
    }

    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable
    {
        Class interfaceClass = method.getDeclaringClass();
        InterfaceInstance interfaceInstance = (InterfaceInstance) interfacesToInstancesIndex.get(interfaceClass);
        Interceptor[] interceptors = interfaceInstance.getInterceptors();
        Object target = interfaceInstance.getTarget();

        Invocation invocation = new InvocationImpl(proxy, method, args, interceptors, target);
        return invocation.invokeNext();
    }

    Object createProxy()
    {
        List interfaces = new ArrayList(interfaceInstances.length);
        for (int i = 0; i < interfaceInstances.length; i++)
        {
            InterfaceInstance interfaceInstance = interfaceInstances[i];
            interfaces.add(interfaceInstance.getInterfaceClass());
        }

        return proxy = Proxy.newProxyInstance(getClass().getClassLoader(),
                (Class[]) interfaces.toArray(new Class[0]),
                this);
    }

    Object getTarget(Class interfaceClass)
    {
        InterfaceInstance interfaceInstance = (InterfaceInstance) interfacesToInstancesIndex.get(interfaceClass);
        return interfaceInstance.getTarget();
    }

    public Interceptor[] getInterceptors(Class interfaceClass)
    {
        InterfaceInstance interfaceInstance = (InterfaceInstance) interfacesToInstancesIndex.get(interfaceClass);
        return interfaceInstance.getInterceptors();
    }
}
