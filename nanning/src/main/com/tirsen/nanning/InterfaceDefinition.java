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
 * <!-- $Id: InterfaceDefinition.java,v 1.2 2002-10-22 18:56:25 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
 */
public class InterfaceDefinition
{
    private Class interfaceClass;
    private List aspectClasses = new ArrayList();
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
     * Adds an interceptor to the chain of interceptors.
     *
     * @param aspectClass
     */
    public void addInterceptor(Class aspectClass)
    {
        aspectClasses.add(aspectClass);
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

    InterfaceInstance newInstance() throws IllegalAccessException, InstantiationException
    {
        InterfaceInstance interfaceInstance = new InterfaceInstance();
        interfaceInstance.setInterface(interfaceClass);
        for (Iterator iterator = aspectClasses.iterator(); iterator.hasNext();)
        {
            Class aspectClass = (Class) iterator.next();
            interfaceInstance.addInterspector(aspectClass.newInstance());
        }
        interfaceInstance.setTarget(targetClass.newInstance());
        return interfaceInstance;
    }
}
