/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.lang.reflect.Method;

/**
 * TODO document InterceptorDefinition
 *
 * <!-- $Id: InterceptorDefinition.java,v 1.6 2002-11-30 22:51:45 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.6 $
 */
public class InterceptorDefinition {
    private final Class interceptorClass;
    private Interceptor statelessInterceptorSingleton;
    private Map mapAttributes;
    private Set negativeCache = new HashSet();
    private Set positiveCache = new HashSet();

    public InterceptorDefinition(Class interceptorClass) {
        this.interceptorClass = interceptorClass;
    }

    public Interceptor newInstance() throws InstantiationException, IllegalAccessException {
        Interceptor instance;
        if (statelessInterceptorSingleton != null) {
            instance = statelessInterceptorSingleton;
        } else if (SingletonInterceptor.class.isAssignableFrom(interceptorClass)) {
            instance = statelessInterceptorSingleton = (Interceptor) interceptorClass.newInstance();
        } else {
            instance = (Interceptor) interceptorClass.newInstance();
        }

        if (instance instanceof DefinitionAwareInterceptor) {
            ((DefinitionAwareInterceptor) instance).setInterceptorDefinition(this);
        }
        return instance;
    }

    public Class getInterceptorClass() {
        return interceptorClass;
    }

    public void setAttribute(String name, Object value) {
        if (mapAttributes == null) mapAttributes = new HashMap();
        mapAttributes.put(name, value);
    }

    public Object getAttribute(String name) {
        if (mapAttributes == null) {
            return null;
        }
        else {
            return mapAttributes.get(name);
        }
    }

    public boolean interceptsMethod(Method method) throws InstantiationException, IllegalAccessException {
        if(negativeCache.contains(method)) {
            return false;
        }
        if(positiveCache.contains(method)) {
            return true;
        }

        Interceptor interceptor = newInstance();
        if (interceptor instanceof FilterMethodsInterceptor) {
            if (((FilterMethodsInterceptor) interceptor).interceptsMethod(method)) {
                positiveCache.add(method);
                return true;
            }
        } else {
            positiveCache.add(method);
            return true;
        }
        negativeCache.add(interceptor);
        return false;
    }
}
