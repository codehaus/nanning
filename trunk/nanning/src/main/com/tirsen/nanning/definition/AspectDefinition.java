/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.definition;

import java.lang.reflect.Method;
import java.util.*;

import com.tirsen.nanning.MethodInterceptor;
import com.tirsen.nanning.MixinInstance;

/**
 * Defines an interface that's to be added to an aspected object.
 *
 * <!-- $Id: AspectDefinition.java,v 1.8 2003-05-22 20:18:32 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.8 $
 *
 * @deprecated please use the new {@link com.tirsen.nanning.config.AspectSystem} framework instead.
 */
public class AspectDefinition {
    private Class interfaceClass;
    private List interceptorDefinitions = new ArrayList();
    private Class targetClass;
    private Map methodsToIndex;

    /**
     * Specify interface to use.
     *
     * @param interfaceClass
     */
    public void setInterface(Class interfaceClass) {
        this.interfaceClass = interfaceClass;
        methodsToIndex = new HashMap();
        Method[] methods = interfaceClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            methodsToIndex.put(method, new Integer(i));
        }
    }

    /**
     * Adds an interceptor to the chain of interceptors. Note: if you use this utility-method (that automatically
     * creates an {@link com.tirsen.nanning.definition.InterceptorDefinition}) stateless interceptors
     *
     * @param interceptorClass
     */
    public void addInterceptor(Class interceptorClass) {
        addInterceptor(new InterceptorDefinition(interceptorClass));
    }

    /**
     * Adds an interceptor to the chain of interceptors.
     *
     * @param interceptorDefinition
     */
    public void addInterceptor(InterceptorDefinition interceptorDefinition) {
        interceptorDefinitions.add(interceptorDefinition);
    }

    /**
     * Specify target-object to use.
     *
     * @param targetClass
     */
    public void setTarget(Class targetClass) {
        this.targetClass = targetClass;
    }

    MixinInstance createMixinInstance()
            throws IllegalAccessException, InstantiationException {
        if (targetClass != null) {
            return newInstance(targetClass.newInstance());
        } else {
            return newInstance(null);
        }
    }

    public Class getInterfaceClass() {
        return interfaceClass;
    }

    MixinInstance newInstance(Object target)
            throws InstantiationException, IllegalAccessException {
        checkTarget(target);

        MixinInstance mixinInstance = new MixinInstance();
        mixinInstance.setInterfaceClass(getInterfaceClass());

        for (Iterator iterator = interceptorDefinitions.iterator(); iterator.hasNext();) {
            InterceptorDefinition interceptorDefinition = (InterceptorDefinition) iterator.next();
            Method[] methods = mixinInstance.getAllMethods();
            for (int j = 0; j < methods.length; j++) {
                Method method = methods[j];
                if (interceptorDefinition.interceptsMethod(method)) {
                    mixinInstance.addInterceptor(method, (MethodInterceptor) interceptorDefinition.newInstance());
                }
            }
        }

        mixinInstance.setTarget(target);
        return mixinInstance;
    }

    private void checkTarget(Object target) {
        if (target == null) {
            return;
        }

        if (!interfaceClass.isInstance(target)) {
            throw new IllegalArgumentException("target does not implement interface: " + target);
        }
        if (!targetClass.isInstance(target)) {
            throw new IllegalArgumentException("target is not an instance of target-class: " + target);
        }
    }

    public List getConstructionInterceptors() {
        List interceptors = new ArrayList();
        for (Iterator iterator = interceptorDefinitions.iterator(); iterator.hasNext();) {
            InterceptorDefinition interceptorDefinition = (InterceptorDefinition) iterator.next();
            if (interceptorDefinition.interceptsConstructor(getInterfaceClass())) {
                interceptors.add(interceptorDefinition.newInstance());
            }
        }
        return interceptors;
    }

    public Collection getInterceptorDefinitions() {
        return interceptorDefinitions;
    }
}
