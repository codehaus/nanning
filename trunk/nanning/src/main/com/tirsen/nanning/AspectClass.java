/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.util.*;
import java.lang.reflect.Proxy;

/**
 * The definition of an aspected object, specifies interfaces, interceptors and target-objects.
 *
 * <!-- $Id: AspectClass.java,v 1.12 2002-11-30 18:23:56 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.12 $
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
        return newInstance(null);
    }

    public Object newInstance(Object[] targets) {
        SideAspectInstance[] sideAspectInstances = instantiateSideAspects(targets);

        AspectInstance aspectInstance = new AspectInstance(this, sideAspectInstances);

        List sideAspects = new ArrayList(sideAspectInstances.length);
        for (int i = 0; i < sideAspectInstances.length; i++)
        {
            SideAspectInstance interfaceInstance = sideAspectInstances[i];
            sideAspects.add(interfaceInstance.getInterfaceClass());
        }

        Object proxy = instantiateProxy(aspectInstance, sideAspects);

        aspectInstance.setProxy(proxy);

        return proxy;
    }

    protected Object instantiateProxy(AspectInstance aspectInstance, List interfaces) {
        Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(),
                        (Class[]) interfaces.toArray(new Class[0]),
                        aspectInstance);
        return proxy;
    }

    SideAspectInstance[] instantiateSideAspects(Object[] targets)
    {
        SideAspectInstance[] sideAspectInstances;
        try
        {
            List instances = new ArrayList(aspectDefinitions.size() + 1);

            // add the class-specific interface, interceptors and target
            SideAspectInstance classInterfaceInstance;
            if (targets != null) {
                classInterfaceInstance = createAspectInstance(new Interceptor[0], targets[0]);
            } else {
                classInterfaceInstance = createAspectInstance(new Interceptor[0]);
            }
            instances.add(classInterfaceInstance);
            Interceptor[] classInterceptors = classInterfaceInstance.getAllInterceptors();

            for (ListIterator iterator = aspectDefinitions.listIterator(); iterator.hasNext();)
            {
                AspectDefinition aspectDefinition = (AspectDefinition) iterator.next();
                SideAspectInstance interfaceInstance = null;
                if (targets != null) {
                    interfaceInstance = aspectDefinition.createAspectInstance(classInterceptors,
                                                                              targets[iterator.previousIndex() + 1]);
                }
                else {
                    interfaceInstance = aspectDefinition.createAspectInstance(classInterceptors);
                }
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
    public void addAspect(AspectDefinition interfaceDefinition)
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
