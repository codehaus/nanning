/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * TODO document Factory
 *
 * <!-- $Id: Factory.java,v 1.1 2002-10-21 21:07:31 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public class Factory
{
    private static Map factories = new HashMap();

    private Class intf;
    private Class defaultTarget;
    public List aspects = new ArrayList();

    public Factory(Class intf)
    {
        this.intf = intf;
    }

    public Object newInstance() throws InstantiationException, IllegalAccessException
    {
        AspectProxy aspectProxy = AspectProxy.create(defaultTarget.newInstance());
        for (Iterator iterator = aspects.iterator(); iterator.hasNext();)
        {
            Class aspectClass = (Class) iterator.next();
            aspectProxy.addAspect((Interceptor) aspectClass.newInstance());
        }
        return aspectProxy.createProxy(new Class[] { intf });
    }

    public static Factory addFactory(Class intf)
    {
        Factory factory = new Factory(intf);
        factories.put(intf, factory);
        return factory;
    }

    public void addAspect(Class aspectClass)
    {
        aspects.add(aspectClass);
    }

    public void setDefaultTarget(Class targetClass)
    {
        this.defaultTarget = targetClass;
    }

    public static Factory getFactory(Class intf)
    {
        return (Factory) factories.get(intf);
    }
}
