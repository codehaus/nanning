/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.util.*;

/**
 * The definition of an aspected object, specifies interfaces, interceptors and target-objects.
 *
 * <!-- $Id: AspectClass.java,v 1.8 2002-11-06 17:50:05 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.8 $
 */
public class AspectClass extends AspectDefinition
{
    private final List aspectDefinitions = new ArrayList();
    private Map interfacesToInstancesIndex;

    /**
     * Instantiates an instance with the specified interfaces, interceptors and target-objects.
     *
     * @return a new aspected object.
     *
     * @throws AspectException
     */
    public Object newInstance()
    {
        AspectInstance aspectInstance =
                new AspectInstance(this);

        return aspectInstance.createProxy();
    }

    SideAspectInstance[] instantiateSideAspects()
    {
        SideAspectInstance[] sideAspectInstances;
        try
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


            sideAspectInstances = (SideAspectInstance[]) instances.toArray(new SideAspectInstance[0]);
        }
        catch (IllegalAccessException e)
        {
            throw new AspectException(e);
        }
        catch (InstantiationException e)
        {
            throw new AspectException(e);
        }
        return sideAspectInstances;
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

        reindexInterfacesToIndex();
    }

    private void reindexInterfacesToIndex()
    {
        interfacesToInstancesIndex = new HashMap();
        indexInterface(getInterfaceClass(), 0);
        for (ListIterator iterator = aspectDefinitions.listIterator(); iterator.hasNext();)
        {
            AspectDefinition aspectDefinition = (AspectDefinition) iterator.next();
            indexInterface(aspectDefinition.getInterfaceClass(), iterator.previousIndex() + 1);
        }
    }

    private void indexInterface(Class interfaceClass, int instanceIndex)
    {
        // when two side-aspects implement same interface the earlier one take precedence
        // that is it already exists in the index
        if(!interfacesToInstancesIndex.containsKey(interfaceClass))
        {
            interfacesToInstancesIndex.put(interfaceClass, new Integer(instanceIndex));
        }
        Class[] interfaces = interfaceClass.getInterfaces();
        for (int i = 0; i < interfaces.length; i++)
        {
            Class superInterface = interfaces[i];
            indexInterface(superInterface, instanceIndex);
        }
    }

    public void setInterface(Class interfaceClass)
    {
        super.setInterface(interfaceClass);
        reindexInterfacesToIndex();
    }

    public int getSideAspectIndexForInterface(Class interfaceClass)
    {
        Integer integer = (Integer) interfacesToInstancesIndex.get(interfaceClass);
        if(integer == null)
        {
            throw new IllegalArgumentException("No such interface for this object: " + interfaceClass.getName());
        }
        return integer.intValue();
    }
}
