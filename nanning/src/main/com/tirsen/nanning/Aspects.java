/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.lang.reflect.Proxy;


/**
 * TODO document Aspects
 *
 * <!-- $Id: Aspects.java,v 1.2 2002-10-22 18:28:09 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
 */
public class Aspects
{
    public static Interceptor[] getInterceptors(Object proxy, Class interfaceClass)
    {
        return getAspectInstance(proxy).getInterceptors(interfaceClass);
    }

    private static AspectInstance getAspectInstance(Object proxy)
    {
        return (AspectInstance) Proxy.getInvocationHandler(proxy);
    }

    public static Object getTarget(Object proxy, Class interfaceClass)
    {
        return getAspectInstance(proxy).getTarget(interfaceClass);
    }
}
