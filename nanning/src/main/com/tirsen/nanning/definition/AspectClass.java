/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.definition;

import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MixinInstance;
import com.tirsen.nanning.AspectException;

import java.util.*;
import java.lang.reflect.Proxy;

/**
 * The definition of an aspected object, specifies interfaces, interceptors and target-objects.
 *
 * <!-- $Id: AspectClass.java,v 1.2 2003-01-18 18:27:26 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
 */
public class AspectClass {
    private final List aspectDefinitions = new ArrayList();
    private Map interfacesToInstancesIndex = new HashMap();
    private AspectRepository aspectRepository;

    public AspectClass() {
        aspectDefinitions.add(new AspectDefinition());
    }

    /**
     * Instantiates an instance with the specified interfaces, interceptors and target-objects.
     *
     * @return a new aspected object.
     *
     * @throws com.tirsen.nanning.AspectException
     */
    public Object newInstance() {
        return newInstance(null);
    }

    public Object newInstance(Object[] targets) {
        AspectInstance aspectInstance = new AspectInstance(getAspectRepository(), getInterfaceClass());
        try {
            // iterate the rest of the definitions and add the interceptors of the first on to the rest
            for (ListIterator iterator = aspectDefinitions.listIterator(); iterator.hasNext();) {
                AspectDefinition mixinDefinition = (AspectDefinition) iterator.next();
                MixinInstance mixinInstance = null;
                if (targets != null) {
                    mixinInstance =
                            mixinDefinition.newInstance(targets[iterator.previousIndex()]);
                } else {
                    mixinInstance =
                            mixinDefinition.createMixinInstance();
                }
                aspectInstance.addMixin(mixinInstance);
            }
        } catch (IllegalAccessException e) {
            throw new AspectException(e);
        } catch (InstantiationException e) {
            throw new AspectException(e);
        }

        List constructionInterceptors = new ArrayList();
        for (Iterator iterator = aspectDefinitions.iterator(); iterator.hasNext();) {
            AspectDefinition mixinDefinition = (AspectDefinition) iterator.next();
            constructionInterceptors.addAll(mixinDefinition.getConstructionInterceptors());
        }

        aspectInstance.setConstructionInterceptors(constructionInterceptors);
        Object proxy = aspectInstance.getProxy();

        return proxy;
    }

    MixinInstance[] instantiateSideAspects(Object[] targets) {
        MixinInstance[] sideAspectInstances;
        try {
            List instances = new ArrayList(aspectDefinitions.size());

            // iterate the rest of the definitions and add the interceptors of the first on to the rest
            for (ListIterator iterator = aspectDefinitions.listIterator(); iterator.hasNext();) {
                AspectDefinition aspectDefinition = (AspectDefinition) iterator.next();
                MixinInstance aspectInstance = null;
                if (targets != null) {
                    aspectInstance =
                            aspectDefinition.newInstance(targets[iterator.previousIndex()]);
                } else {
                    aspectInstance =
                            aspectDefinition.createMixinInstance();
                }
                instances.add(aspectInstance);
            }

            sideAspectInstances = (MixinInstance[]) instances.toArray(new MixinInstance[0]);
        } catch (IllegalAccessException e) {
            throw new AspectException(e);
        } catch (InstantiationException e) {
            throw new AspectException(e);
        }
        return sideAspectInstances;
    }

    private AspectDefinition getClassAspectDefinition() {
        return getAspectDefinition(0);
    }

    /**
     * Adds a new interface specification, specifies interface, interceptors and target-object these are stacked
     * "on the side" of this object.
     *
     * @param interfaceDefinition
     */
    public void addAspect(AspectDefinition interfaceDefinition) {
        aspectDefinitions.add(interfaceDefinition);

        reindexInterfacesToIndex();
    }

    private void reindexInterfacesToIndex() {
        interfacesToInstancesIndex.clear();
        for (ListIterator iterator = aspectDefinitions.listIterator(); iterator.hasNext();) {
            AspectDefinition aspectDefinition = (AspectDefinition) iterator.next();
            indexInterface(aspectDefinition.getInterfaceClass(), iterator.previousIndex());
        }
    }

    public Class getInterfaceClass() {
        return getClassAspectDefinition().getInterfaceClass();
    }

    private void indexInterface(Class interfaceClass, int instanceIndex) {
        // when two side-aspects implement same interface the earlier one take precedence
        // that is it already exists in the index
        if (!interfacesToInstancesIndex.containsKey(interfaceClass)) {
            interfacesToInstancesIndex.put(interfaceClass, new Integer(instanceIndex));
        }
        Class[] interfaces = interfaceClass.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            Class superInterface = interfaces[i];
            indexInterface(superInterface, instanceIndex);
        }
    }

    public void setInterface(Class interfaceClass) {
        getAspectDefinition(0).setInterface(interfaceClass);
        reindexInterfacesToIndex();
    }

    public int getSideAspectIndexForInterface(Class interfaceClass) {
        Integer integer = (Integer) interfacesToInstancesIndex.get(interfaceClass);
        if (integer == null) {
            throw new IllegalArgumentException("No such interface for this object: " + interfaceClass.getName());
        }
        return integer.intValue();
    }

    public void addInterceptor(Class aClass) {
        getClassAspectDefinition().addInterceptor(aClass);
    }

    private AspectDefinition getAspectDefinition(int index) {
        return (AspectDefinition) aspectDefinitions.get(index);
    }

    public void setTarget(Class aClass) {
        getClassAspectDefinition().setTarget(aClass);
    }

    public void addInterceptor(InterceptorDefinition interceptorDefinition) {
        getClassAspectDefinition().addInterceptor(interceptorDefinition);
    }

    public AspectRepository getAspectRepository() {
        return aspectRepository;
    }

    public void setAspectRepository(AspectRepository aspectRepository) {
        this.aspectRepository = aspectRepository;
    }

    public List getAspectDefinitions() {
        return aspectDefinitions;
    }
}
