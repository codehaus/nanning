/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 * (C) 2003 Jon Tirsen
 */
package org.codehaus.nanning;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * Utility for accessing and modifying aspected object.
 *
 * <!-- $Id: Aspects.java,v 1.2 2003-07-12 16:48:16 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.2 $
 */
public class Aspects {
    private static ThreadLocal contextAspectFactory = new InheritableThreadLocal();
    private static ThreadLocal currentThis = new InheritableThreadLocal();

    /**
     * Gets the interceptors that belongs to the proxy.
     *
     * @param proxy
     * @return the interceptors.
     */
    public static Collection getInterceptors(Object proxy) {
        return getAspectInstance(proxy).getAllInterceptors();
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

    /**
     * Gets the AspectInstance of the given aspected object.
     * @param proxy
     * @return
     */
    public static AspectInstance getAspectInstance(Object proxy) {
        return (AspectInstance) Proxy.getInvocationHandler(proxy);
    }

    /**
     * Sets the target of the mixin with the specified interface.
     * @param proxy
     * @param interfaceClass
     * @param target
     */
    public static void setTarget(Object proxy, Class interfaceClass, Object target) {
        getAspectInstance(proxy).setTarget(interfaceClass, target);
    }

    public static boolean isAspectObject(Object o) {
        return o == null ? false : Proxy.isProxyClass(o.getClass());
    }

    public static Object[] getTargets(Object object) {
        return object == null ? null : Aspects.getAspectInstance(object).getTargets();
    }

    public static AspectFactory getCurrentAspectFactory() {
        if (getThis() != null) {
            return getAspectInstance(getThis()).getAspectFactory();
        } else {
            return (AspectFactory) contextAspectFactory.get();
        }
    }

    public static void setContextAspectFactory(AspectFactory factory) {
        contextAspectFactory.set(factory);
    }

    /**
     * Given a proxy-class returns the first real interface it implements.
     *
     * @param proxyClass proxyClass to inspect.
     * @return first real interface implemented by proxyClass.
     */
    public static Class getRealClass(Class proxyClass) {
        if (!Proxy.isProxyClass(proxyClass)) {
            return proxyClass;
        }
        Class[] interfaces = proxyClass.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            Class anInterface = interfaces[i];
            Class realClass = getRealClass(anInterface);
            if (realClass != null) {
                return realClass;
            }
        }
        return null;
    }

    /**
     * Gets the currently executing aspected object, aspected objects should use this method
     * instead of <code>this</code>.
     * @return
     */
    public static Object getThis() {
        return currentThis.get();
    }

    static void setThis(Object proxy) {
        Aspects.currentThis.set(proxy);
    }
}
