/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * The definition of an aspected object, specifies interfaces, interceptors and target-objects.
 *
 * <!-- $Id: AspectClass.java,v 1.5 2002-10-28 18:51:00 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.5 $
 */
public class AspectClass extends AspectDefinition
{
    private final List aspectDefinitions = new ArrayList();

    /**
     * Instantiates an instance with the specified interfaces, interceptors and target-objects.
     *
     * @return a new aspected object.
     *
     * @throws InstantiationException
     */
    public Object newInstance() throws InstantiationException, IllegalAccessException
    {
        List instances = new ArrayList(aspectDefinitions.size() + 1);

        // add the class-specific interface, interceptors and target
        SideAspectInstance classInterfaceInstance = createInterfaceInstance();
        instances.add(classInterfaceInstance);
        List proxyInterceptors = Arrays.asList(classInterfaceInstance.getInterceptors());

        for (Iterator iterator = aspectDefinitions.iterator(); iterator.hasNext();)
        {
            AspectDefinition interfaceDefinition = (AspectDefinition) iterator.next();
            SideAspectInstance interfaceInstance = interfaceDefinition.createInterfaceInstance();

            // add the interceptors for the class _before_ those defined for the side-aspect
            int size = proxyInterceptors.size() + interfaceInstance.getInterceptors().length;
            List interfaceInterceptors = new ArrayList(size);
            interfaceInterceptors.addAll(proxyInterceptors);
            interfaceInterceptors.addAll(Arrays.asList(interfaceInstance.getInterceptors()));
            interfaceInstance.setInterceptors((Interceptor[]) interfaceInterceptors.toArray(new Interceptor[size]));

            instances.add(interfaceInstance);
        }


        AspectInstance aspectInstance =
                new AspectInstance((SideAspectInstance[]) instances.toArray(new SideAspectInstance[0]));

        return aspectInstance.createProxy();
    }

    /**
     * Adds a new interface specification, specifies interface, interceptors and target-object these are stacked
     * "on the side" of this object.
     *
     * @param interfaceDefinition
     */
    public void addSideAspect(AspectDefinition interfaceDefinition)
    {
        aspectDefinitions.add(interfaceDefinition);
    }
}
