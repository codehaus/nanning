/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.util.*;
import java.lang.reflect.Method;

/**
 * Defines an interface that's to be added to an aspected object.
 *
 * <!-- $Id: AspectDefinition.java,v 1.6 2002-11-22 17:22:09 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.6 $
 */
public class AspectDefinition
{
    private Class interfaceClass;
    private final List interceptorDefinitions = new ArrayList();
    private Class targetClass;
    private Map methodsToIndex;

    /**
     * Specify interface to use.
     *
     * @param interfaceClass
     */
    public void setInterface(Class interfaceClass)
    {
        this.interfaceClass = interfaceClass;
        methodsToIndex = new HashMap();
        Method[] methods = interfaceClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            methodsToIndex.put(method, new Integer(i));
        }
    }

    /**
     * Adds an interceptor to the chain of interceptors. Note: if you use this utility-method (that automatically
     * creates an {@link InterceptorDefinition}) stateless interceptors 
     *
     * @param interceptorClass
     */
    public void addInterceptor(Class interceptorClass)
    {
        addInterceptor(new InterceptorDefinition(interceptorClass));
    }

    /**
     * Adds an interceptor to the chain of interceptors.
     *
     * @param interceptorDefinition
     */
    public void addInterceptor(InterceptorDefinition interceptorDefinition)
    {
        interceptorDefinitions.add(interceptorDefinition);
    }

    /**
     * Specify target-object to use.
     *
     * @param targetClass
     */
    public void setTarget(Class targetClass)
    {
        this.targetClass = targetClass;
    }

    public Class getTarget() {
        return targetClass;
    }

    SideAspectInstance createAspectInstance(Interceptor[] topInterceptors)
            throws IllegalAccessException, InstantiationException
    {
        SideAspectInstance sideAspectInstance = new SideAspectInstance(this);
        sideAspectInstance.setInterface(interfaceClass);

        List instances = new ArrayList(interceptorDefinitions.size() + topInterceptors.length);
        instances.addAll(Arrays.asList(topInterceptors));
        for (Iterator iterator = interceptorDefinitions.iterator(); iterator.hasNext();)
        {
            InterceptorDefinition interceptorDefinition = (InterceptorDefinition) iterator.next();
            instances.add(interceptorDefinition.newInstance());
        }
        Interceptor[] interceptors = (Interceptor[]) instances.toArray(new Interceptor[instances.size()]);
        sideAspectInstance.setInterceptors(interceptors);

        sideAspectInstance.setTarget(targetClass.newInstance());
        return sideAspectInstance;
    }

    public Class getInterfaceClass()
    {
        return interfaceClass;
    }

    public int getMethodIndex(Method method) {
        return ((Integer) methodsToIndex.get(method)).intValue();
    }
}
