/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

/**
 * TODO document InterceptorDefinition
 *
 * <!-- $Id: InterceptorDefinition.java,v 1.4 2002-11-17 14:03:34 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.4 $
 */
public class InterceptorDefinition
{
    private final Class interceptorClass;
    private Interceptor statelessInterceptorSingleton;

    public InterceptorDefinition(Class interceptorClass)
    {
        this.interceptorClass = interceptorClass;
    }

    public Interceptor newInstance() throws InstantiationException, IllegalAccessException
    {
        if(statelessInterceptorSingleton != null)
        {
            return statelessInterceptorSingleton;
        }
        else if(SingletonInterceptor.class.isAssignableFrom(interceptorClass))
        {
            return statelessInterceptorSingleton = (Interceptor) interceptorClass.newInstance();
        }
        else
        {
            return (Interceptor) interceptorClass.newInstance();
        }
    }

    public Class getInterceptorClass()
    {
        return interceptorClass;
    }
}
