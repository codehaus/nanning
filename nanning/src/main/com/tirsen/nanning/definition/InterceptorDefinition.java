/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.definition;

import com.tirsen.nanning.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.lang.reflect.Method;

/**
 * TODO document InterceptorDefinition
 *
 * <!-- $Id: InterceptorDefinition.java,v 1.1 2003-01-12 13:25:40 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public class InterceptorDefinition {
    private final Class interceptorClass;
    private Interceptor singletonInterceptor;
    private Map mapAttributes;
    private Set negativeCache = new HashSet();
    private Set positiveCache = new HashSet();

    public InterceptorDefinition(Class interceptorClass) {
        if(!Interceptor.class.isAssignableFrom(interceptorClass)) {
            throw new IllegalArgumentException(interceptorClass + " is not an interceptor.");
        }
        this.interceptorClass = interceptorClass;
    }

    public Interceptor newInstance() {
        if(singletonInterceptor != null) {
            return singletonInterceptor;
        }
        try {
            Interceptor instance = (Interceptor) interceptorClass.newInstance();
            if (SingletonInterceptor.class.isAssignableFrom(interceptorClass)) {
                singletonInterceptor = instance;
            }

            if (instance instanceof DefinitionAwareInterceptor) {
                ((DefinitionAwareInterceptor) instance).setInterceptorDefinition(this);
            }
            return instance;
        } catch (Exception e) {
            throw new AspectException(e);
        }
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
        } else {
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
        } else if(interceptor instanceof MethodInterceptor) {
            positiveCache.add(method);
            return true;
        }
        negativeCache.add(interceptor);
        return false;
    }

    public boolean interceptsConstructor(Class interfaceClass) {
        Interceptor interceptor = newInstance();
        if(interceptor instanceof ConstructionInterceptor) {
            return ((ConstructionInterceptor) interceptor).interceptsConstructor(interfaceClass);
        }
        return false;
    }

    public Interceptor getSingleton() {
        if(singletonInterceptor == null) {
            newInstance();
        }
        if(singletonInterceptor == null) {
            throw new IllegalStateException("This is not a singleton-interceptor: " + interceptorClass);
        }

        return singletonInterceptor;
    }
}
