/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.lang.reflect.Proxy;


/**
 * Facade for accessing some important features of aspected objects and their definitions.
 *
 * <!-- $Id: Aspects.java,v 1.4 2002-10-23 21:26:43 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.4 $
 */
public class Aspects
{
    /**
     * Gets the interceptors that belongs to the proxy
     *
     * @param proxy
     * @return the interceptors.
     */
    public static Interceptor[] getInterceptors(Object proxy)
    {
        return getAspectInstance(proxy).getProxyInterceptors();
    }

    /**
     * What interceptors does the aspected object have for the given interface.
     *
     * @param proxy
     * @param interfaceClass
     * @return the interceptors.
     */
    public static Interceptor[] getInterceptors(Object proxy, Class interfaceClass)
    {
        return getAspectInstance(proxy).getInterceptors(interfaceClass);
    }

    /**
     * What is the target-object for the given interface.
     *
     * @param proxy
     * @param interfaceClass
     * @return the target-object.
     */
    public static Object getTarget(Object proxy, Class interfaceClass)
    {
        return getAspectInstance(proxy).getTarget(interfaceClass);
    }

    private static AspectInstance getAspectInstance(Object proxy)
    {
        return (AspectInstance) Proxy.getInvocationHandler(proxy);
    }
}
