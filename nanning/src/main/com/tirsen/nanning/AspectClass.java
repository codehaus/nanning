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
 * The definition of an aspected object, specifies interfaces, interceptors and target-objects.
 *
 * <!-- $Id: AspectClass.java,v 1.2 2002-10-22 18:56:25 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
 */
public class AspectClass
{
    private List interfaceDefinitions = new ArrayList();

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
    public Object newInstance() throws InstantiationException
    {
        List instances = new ArrayList(interfaceDefinitions.size());
        for (Iterator iterator = interfaceDefinitions.iterator(); iterator.hasNext();)
        {
            InterfaceDefinition interfaceDefinition = (InterfaceDefinition) iterator.next();
            try
            {
                instances.add(interfaceDefinition.newInstance());
            }
            catch (IllegalAccessException e)
            {
                throw new InstantiationException(e.getMessage());
            }
        }

        AspectInstance aspectInstance =
                new AspectInstance(this, (InterfaceInstance[]) instances.toArray(new InterfaceInstance[0]));

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
}
