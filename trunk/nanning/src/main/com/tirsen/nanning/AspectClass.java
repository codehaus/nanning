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

/**
 * TODO document AspectClass
 *
 * <!-- $Id: AspectClass.java,v 1.1 2002-10-22 18:28:09 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public class AspectClass
{
    public List interfaceDefinitions = new ArrayList();

    AspectClass()
    {
    }

    public static AspectClass create()
    {
        AspectClass aspectClass = new AspectClass();
        return aspectClass;
    }

    public Object newInstance() throws InstantiationException, IllegalAccessException
    {
        List instances = new ArrayList(interfaceDefinitions.size());
        for (Iterator iterator = interfaceDefinitions.iterator(); iterator.hasNext();)
        {
            InterfaceDefinition interfaceDefinition = (InterfaceDefinition) iterator.next();
            instances.add(interfaceDefinition.newInstance());
        }

        AspectInstance aspectInstance =
                new AspectInstance(this, (InterfaceInstance[]) instances.toArray(new InterfaceInstance[0]));

        return aspectInstance.createProxy();
    }

    public void addInterface(InterfaceDefinition interfaceDefinition)
    {
        interfaceDefinitions.add(interfaceDefinition);
    }
}
