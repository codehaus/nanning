/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Defines an interface that's to be added to an aspected object.
 *
 * <!-- $Id: AspectDefinition.java,v 1.4 2002-11-17 14:03:33 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.4 $
 */
public class AspectDefinition
{
    private Class interfaceClass;
    private final List interceptorDefinitions = new ArrayList();
    private Class targetClass;

    /**
     * Specify interface to use.
     *
     * @param interfaceClass
     */
    public void setInterface(Class interfaceClass)
    {
        this.interfaceClass = interfaceClass;
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

    SideAspectInstance createInterfaceInstance() throws IllegalAccessException, InstantiationException
    {
        SideAspectInstance interfaceInstance = new SideAspectInstance();
        interfaceInstance.setInterface(interfaceClass);

        List instances = new ArrayList(interceptorDefinitions.size());
        for (Iterator iterator = interceptorDefinitions.iterator(); iterator.hasNext();)
        {
            InterceptorDefinition interceptorDefinition = (InterceptorDefinition) iterator.next();
            instances.add(interceptorDefinition.newInstance());
        }
        Interceptor[] interceptors = (Interceptor[]) instances.toArray(new Interceptor[instances.size()]);
        interfaceInstance.setInterceptors(interceptors);

        interfaceInstance.setTarget(targetClass.newInstance());
        return interfaceInstance;
    }

    public Class getInterfaceClass()
    {
        return interfaceClass;
    }
}
