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
 * TODO document InterfaceDefinition
 *
 * <!-- $Id: InterfaceDefinition.java,v 1.1 2002-10-22 18:28:09 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public class InterfaceDefinition
{
    private Class interfaceClass;
    public List aspectClasses = new ArrayList();
    private Class targetClass;

    public void setInterface(Class interfaceClass)
    {
        this.interfaceClass = interfaceClass;
    }

    public void addInterceptor(Class aspectClass)
    {
        aspectClasses.add(aspectClass);
    }

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
