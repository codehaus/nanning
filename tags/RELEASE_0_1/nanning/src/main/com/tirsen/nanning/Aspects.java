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
 * <!-- $Id: Aspects.java,v 1.11 2002-12-11 15:11:55 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.11 $
 */
public class Aspects
{
    private static ThreadLocal contextAspectRepository = new ThreadLocal();

    /**
     * Gets the interceptors that belongs to the proxy
     *
     * @param proxy
     * @return the interceptors.
     */
    public static Interceptor[] getInterceptors(Object proxy)
    {
        return getAspectInstance(proxy).getClassInterceptors();
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

    static AspectInstance getAspectInstance(Object proxy)
    {
        return (AspectInstance) Proxy.getInvocationHandler(proxy);
    }

    public static void setTarget(Object proxy, Class interfaceClass, Object target)
    {
        getAspectInstance(proxy).setTarget(interfaceClass, target);
    }

    public static Object getThis()
    {
        return AspectInstance.currentThis.get();
    }

    public static boolean isAspectObject(Object o) {
        return o == null ? true : Proxy.isProxyClass(o.getClass());
    }

    public static Object getClassTarget(Object value) {
        return value == null ? null : getTarget(value, getAspectInstance(value).getAspectClass().getInterfaceClass());
    }

    public static Object[] getTargets(Object object) {
        return object == null ? null : Aspects.getAspectInstance(object).getTargets();
    }

    /**
     * TODO only search class-interceptors at the moment.
     * @param o
     * @param interceptorClass
     * @return
     */
    public static Interceptor findInterceptorByClass(Object o, Class interceptorClass) {
        Interceptor[] interceptors = getAspectInstance(o).getClassInterceptors();
        for (int i = 0; i < interceptors.length; i++) {
            Interceptor interceptor = interceptors[i];
            if(interceptorClass.isInstance(interceptor)) {
                return interceptor;
            }
        }
        return null;
    }

    public static AspectClass getAspectClass(Object proxy) {
        return getAspectInstance(proxy).getAspectClass();
    }

    public static AspectRepository getCurrentAspectRepository() {
        if(getThis() != null) {
            return getAspectInstance(getThis()).getAspectClass().getAspectRepository();
        } else {
            return (AspectRepository) contextAspectRepository.get();
        }
    }

    public static void setContextAspectRepository(AspectRepository aspectRepository) {
        contextAspectRepository.set(aspectRepository);
    }
}
