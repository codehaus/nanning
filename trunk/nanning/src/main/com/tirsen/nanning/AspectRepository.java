/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import com.tirsen.nanning.test.Intf;

import java.util.Map;
import java.util.HashMap;

/**
 * TODO document AspectRepository
 *
 * <!-- $Id: AspectRepository.java,v 1.1 2002-10-27 12:36:41 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public class AspectRepository
{
    public Map interceptorDefinitions = new HashMap();
    public Map aspectDefinitions = new HashMap();
    public Map aspectClasses = new HashMap();

    public void defineInterceptor(InterceptorDefinition interceptorDefinition)
    {
        interceptorDefinitions.put(interceptorDefinition.getInterceptorClass(), interceptorDefinition);
    }

    public InterceptorDefinition getInterceptor(Class interceptorClass)
    {
        return (InterceptorDefinition) interceptorDefinitions.get(interceptorClass);
    }

    public void defineAspect(AspectDefinition aspectDefinition)
    {
        aspectDefinitions.put(aspectDefinition.getInterfaceClass(), aspectDefinition);
    }

    public AspectDefinition getAspect(Class interfaceClass)
    {
        return (AspectDefinition) aspectDefinitions.get(interfaceClass);
    }

    public void defineClass(AspectClass aspectClass)
    {
        aspectClasses.put(aspectClass.getInterfaceClass(), aspectClass);
    }

    public AspectClass getClass(Class interfaceClass)
    {
        return (AspectClass) aspectClasses.get(interfaceClass);
    }

    public Object newInstance(Class aspectInterface) throws InstantiationException, IllegalAccessException
    {
        return getClass(aspectInterface).newInstance();
    }
}
