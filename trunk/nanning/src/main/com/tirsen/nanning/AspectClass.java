/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.Arrays;

/**
 * The definition of an aspected object, specifies interfaces, interceptors and target-objects.
 *
 * <!-- $Id: AspectClass.java,v 1.3 2002-10-23 21:26:43 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.3 $
 */
public class AspectClass
{
    private List interfaceDefinitions = new ArrayList();
    private List interceptorDefinitions = new ArrayList();

    AspectClass()
    {
    }

    /**
     * Creates an unconfigured class.
     *
     * @return a new unconfigured class.
     */
    public static AspectClass create()
    {
        AspectClass aspectClass = new AspectClass();
        return aspectClass;
    }

    /**
     * Instantiates an instance with the specified interfaces, interceptors and target-objects.
     *
     * @return a new aspected object.
     *
     * @throws InstantiationException
     */
    public Object newInstance() throws InstantiationException, IllegalAccessException
    {
        List proxyInterceptors = new ArrayList(interceptorDefinitions.size());
        for (Iterator iterator = interceptorDefinitions.iterator(); iterator.hasNext();)
        {
            Class interceptorClass = (Class) iterator.next();
            proxyInterceptors.add(interceptorClass.newInstance());
        }

        List instances = new ArrayList(interfaceDefinitions.size());
        for (Iterator iterator = interfaceDefinitions.iterator(); iterator.hasNext();)
        {
            InterfaceDefinition interfaceDefinition = (InterfaceDefinition) iterator.next();
            InterfaceInstance interfaceInstance = interfaceDefinition.newInstance();
            // add the interceptors for the whole proxy _before_ those defined for the aspect
            int size = proxyInterceptors.size() + interfaceInstance.getInterceptors().length;
            List interfaceInterceptors =
                    new ArrayList(size);
            interfaceInterceptors.addAll(proxyInterceptors);
            interfaceInterceptors.addAll(Arrays.asList(interfaceInstance.getInterceptors()));
            interfaceInstance.setInterceptors((Interceptor[]) interfaceInterceptors.toArray(new Interceptor[size]));
            instances.add(interfaceInstance);
        }

        AspectInstance aspectInstance =
                new AspectInstance((Interceptor[]) proxyInterceptors.toArray(new Interceptor[0]),
                        (InterfaceInstance[]) instances.toArray(new InterfaceInstance[0]));

        return aspectInstance.createProxy();
    }

    /**
     * Adds a new interface specification, specifies interface, interceptors and target-object these are stacked
     * "on the side" of the object.
     *
     * @param interfaceDefinition
     */
    public void addInterface(InterfaceDefinition interfaceDefinition)
    {
        interfaceDefinitions.add(interfaceDefinition);
    }

    public void addInterceptor(Class interceptorClass)
    {
        interceptorDefinitions.add(interceptorClass);
    }
}
