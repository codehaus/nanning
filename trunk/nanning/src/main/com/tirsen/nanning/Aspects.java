/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Facade for accessing some important features of aspected objects and their definitions.
 *
 * <!-- $Id: Aspects.java,v 1.17 2003-02-20 15:35:58 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.17 $
 */
public class Aspects {
    private static ThreadLocal contextAspectRepository = new InheritableThreadLocal();
    static ThreadLocal currentThis = new ThreadLocal();

    /**
     * Gets the interceptors that belongs to the proxy
     *
     * @param proxy
     * @return the interceptors.
     */
    public static Interceptor[] getInterceptors(Object proxy) {
        Set interceptors = getAspectInstance(proxy).getAllInterceptors();
        return (Interceptor[]) interceptors.toArray(new Interceptor[interceptors.size()]);
    }

    /**
     * What interceptors does the aspected object have for the given interface.
     *
     * @param proxy
     * @param interfaceClass
     * @return the interceptors.
     */
    public static Interceptor[] getInterceptors(Object proxy, Class interfaceClass) {
        Set interceptors = getAspectInstance(proxy).getInterceptors(interfaceClass);
        return (Interceptor[]) interceptors.toArray(new Interceptor[interceptors.size()]);
    }

    /**
     * What is the target-object for the given interface.
     *
     * @param proxy
     * @param interfaceClass
     * @return the target-object.
     */
    public static Object getTarget(Object proxy, Class interfaceClass) {
        return getAspectInstance(proxy).getTarget(interfaceClass);
    }

    public static AspectInstance getAspectInstance(Object proxy) {
        AspectInstance aspectInstance = (AspectInstance) Proxy.getInvocationHandler(proxy);
        assert aspectInstance != null;
        return aspectInstance;
    }

    public static void setTarget(Object proxy, Class interfaceClass, Object target) {
        getAspectInstance(proxy).setTarget(interfaceClass, target);
    }

    public static Object getThis() {
        return currentThis.get();
    }

    public static boolean isAspectObject(Object o) {
        return o == null ? false : Proxy.isProxyClass(o.getClass());
    }

    public static Object[] getTargets(Object object) {
        return object == null ? null : Aspects.getAspectInstance(object).getTargets();
    }

    public static Interceptor findFirstInterceptorWithClass(Object proxy, Class interceptorClass) {
        Set allInterceptors = getAspectInstance(proxy).getAllInterceptors();
        for (Iterator iterator = allInterceptors.iterator(); iterator.hasNext();) {
            Interceptor interceptor = (Interceptor) iterator.next();
            if (interceptorClass.isInstance(interceptor)) {
                return interceptor;
            }
        }
        return null;
    }

    public static AspectFactory getCurrentAspectFactory() {
        if (getThis() != null) {
            return getAspectInstance(getThis()).getAspectFactory();
        } else {
            return (AspectFactory) contextAspectRepository.get();
        }
    }

    public static void setContextAspectFactory(AspectFactory aspectRepository) {
        contextAspectRepository.set(aspectRepository);
    }

    public static MethodInterceptor[] getInterceptors(Object proxy, Method method) {
        List interceptors = getAspectInstance(proxy).getInterceptorsForMethod(method);
        return (MethodInterceptor[]) interceptors.toArray(new MethodInterceptor[interceptors.size()]);
    }
}
