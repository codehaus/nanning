/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.util.*;
import java.lang.reflect.Method;

/**
 * Defines an interface that's to be added to an aspected object.
 *
 * <!-- $Id: AspectDefinition.java,v 1.8 2002-11-30 22:51:45 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.8 $
 */
public class AspectDefinition
{
    private Class interfaceClass;
    private InterceptorDefinition[] interceptorDefinitions = new InterceptorDefinition[0];
    private Class targetClass;
    private Map methodsToIndex;

    /**
     * Specify interface to use.
     *
     * @param interfaceClass
     */
    public void setInterface(Class interfaceClass)
    {
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
     * creates an {@link InterceptorDefinition}) stateless interceptors
     *
     * @param interceptorClass
     */
    public void addInterceptor(Class interceptorClass)
    {
        addInterceptor(new InterceptorDefinition(interceptorClass));
    }

    /**
     * Adds an interceptor to the chain of interceptors.
     *
     * @param interceptorDefinition
     */
    public void addInterceptor(InterceptorDefinition interceptorDefinition)
    {
        List result = new ArrayList((interceptorDefinitions == null ? 0 : interceptorDefinitions.length) + 1);
        result.addAll(Arrays.asList(interceptorDefinitions));
        result.add(interceptorDefinition);
        interceptorDefinitions = (InterceptorDefinition[]) result.toArray(new InterceptorDefinition[result.size()]);
    }

    /**
     * Specify target-object to use.
     *
     * @param targetClass
     */
    public void setTarget(Class targetClass)
    {
        this.targetClass = targetClass;
    }

    public Class getTarget() {
        return targetClass;
    }

    SideAspectInstance createAspectInstance(InterceptorDefinition[] classInterceptorDefinitions,
                                            Interceptor[] classInterceptors)
            throws IllegalAccessException, InstantiationException
    {
        return createAspectInstance(classInterceptorDefinitions,
                classInterceptors, targetClass.newInstance());
    }

    public Class getInterfaceClass()
    {
        return interfaceClass;
    }

    public int getMethodIndex(Method method) {
        return ((Integer) methodsToIndex.get(method)).intValue();
    }

    SideAspectInstance createAspectInstance(InterceptorDefinition[] classInterceptorDefinitions,
                                            Interceptor[] classInterceptors, Object target)
            throws InstantiationException, IllegalAccessException {

        checkTarget(target);

        SideAspectInstance sideAspectInstance = new SideAspectInstance(this);
        sideAspectInstance.setInterface(interfaceClass);

        List instances = new ArrayList(interceptorDefinitions.length + classInterceptors.length);
        instances.addAll(Arrays.asList(classInterceptors));
        for (int i = 0; i < interceptorDefinitions.length; i++) {
            InterceptorDefinition interceptorDefinition = interceptorDefinitions[i];
            instances.add(interceptorDefinition.newInstance());
        }
        Interceptor[] interceptors = (Interceptor[]) instances.toArray(new Interceptor[instances.size()]);
        List allDefinitionsList =
                new ArrayList(classInterceptorDefinitions.length + interceptorDefinitions.length);
        allDefinitionsList.addAll(Arrays.asList(classInterceptorDefinitions));
        allDefinitionsList.addAll(Arrays.asList(interceptorDefinitions));
        InterceptorDefinition[] allDefinitions =
                (InterceptorDefinition[]) allDefinitionsList.toArray(
                        new InterceptorDefinition[allDefinitionsList.size()]);
        sideAspectInstance.setInterceptors(allDefinitions, interceptors);

        sideAspectInstance.setTarget(target);
        return sideAspectInstance;
    }

    void checkTarget(Object target) {
        assert interfaceClass.isInstance(target) : "target does not implement interface: " + target;
        assert targetClass.isInstance(target) : "target is not an instance of target-class: " + target;
    }

    protected InterceptorDefinition[] getInterceptorDefinitions() {
        return interceptorDefinitions;
    }
}
