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
 * <!-- $Id: AspectDefinition.java,v 1.1 2002-10-27 12:13:18 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public class AspectDefinition
{
    protected Class interfaceClass;
    protected List interceptorClasses = new ArrayList();
    protected Class targetClass;

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
     * Adds an interceptor to the chain of interceptors.
     *
     * @param aspectClass
     */
    public void addInterceptor(Class aspectClass)
    {
        interceptorClasses.add(aspectClass);
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

        List instances = new ArrayList(interceptorClasses.size());
        for (Iterator iterator = interceptorClasses.iterator(); iterator.hasNext();)
        {
            Class aspectClass = (Class) iterator.next();
            instances.add(aspectClass.newInstance());
        }
        Interceptor[] interceptors = (Interceptor[]) instances.toArray(new Interceptor[instances.size()]);
        interfaceInstance.setInterceptors(interceptors);

        interfaceInstance.setTarget(targetClass.newInstance());
        return interfaceInstance;
    }
}
