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
 * <!-- $Id: Aspects.java,v 1.1 2002-10-21 21:07:31 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public class Aspects
{
    public static Interceptor[] getAspects(Object proxy)
    {
        return getAspectProxy(proxy).getAspects();
    }

    private static AspectProxy getAspectProxy(Object proxy)
    {
        return (AspectProxy) Proxy.getInvocationHandler(proxy);
    }

    public static Object getTarget(Object proxy)
    {
        return getAspectProxy(proxy).getTarget();
    }
}
