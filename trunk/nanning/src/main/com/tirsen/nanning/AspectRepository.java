/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO document AspectRepository
 *
 * <!-- $Id: AspectRepository.java,v 1.3 2002-10-30 13:27:42 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.3 $
 */
public class AspectRepository
{
    private final Map interceptorDefinitions = new HashMap();
    private final Map aspectDefinitions = new HashMap();
    private final Map aspectClasses = new HashMap();

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

    public Object newInstance(Class aspectInterface)
    {
        return getClass(aspectInterface).newInstance();
    }
}
