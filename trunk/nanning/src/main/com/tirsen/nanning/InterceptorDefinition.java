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
 * <!-- $Id: InterceptorDefinition.java,v 1.2 2002-10-28 18:51:00 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
 */
public class InterceptorDefinition
{
    private final Class interceptorClass;

    public InterceptorDefinition(Class interceptorClass)
    {
        this.interceptorClass = interceptorClass;
    }

    public Interceptor newInstance() throws InstantiationException, IllegalAccessException
    {
        return (Interceptor) interceptorClass.newInstance();
    }

    public Class getInterceptorClass()
    {
        return interceptorClass;
    }
}
