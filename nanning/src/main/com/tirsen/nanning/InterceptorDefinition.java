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
 * <!-- $Id: InterceptorDefinition.java,v 1.1 2002-10-27 12:36:41 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public class InterceptorDefinition
{
    private Class interceptorClass;

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
