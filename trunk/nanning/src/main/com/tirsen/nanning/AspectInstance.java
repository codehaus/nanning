/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import com.tirsen.nanning.definition.AspectRepository;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * TODO document AspectInstance
 *
 * <!-- $Id: AspectInstance.java,v 1.24 2003-01-24 13:29:29 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.24 $
 */
public class AspectInstance implements InvocationHandler, Externalizable {
    private Object proxy;
    private Map mixins = new HashMap();

    /**
     * Used during serialization only.
     */
    private Object serializeClassIdentifier;
    /**
     * Used during serialization only.
     */
    private Object[] serializeTargets;


    private List mixinsList = new ArrayList();
    private AspectFactory aspectFactory;
    private Object classIdentifier;
    private List constructionInterceptors;

    public AspectInstance() {
    }

    public AspectInstance(AspectRepository aspectRepository, Object classIdentifier) {
        this.aspectFactory = aspectRepository;
        this.classIdentifier = classIdentifier;
    }

    public Object createProxy() {
        if (proxy == null) {
            Set interfaces = getInterfaceClasses();
            proxy = Proxy.newProxyInstance(getClass().getClassLoader(),
                    (Class[]) interfaces.toArray(new Class[0]),
                    this);
        }
        return proxy;
    }

    public void setConstructionInterceptors(List constructionInterceptors) {
        this.constructionInterceptors = constructionInterceptors;
    }

    private Set getInterfaceClasses() {
        Set interfaces = new HashSet(CollectionUtils.collect(mixins.values(), new Transformer() {
            public Object transform(Object o) {
                return ((MixinInstance) o).getInterfaceClass();
            }
        }));
        return interfaces;
    }

    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        Class interfaceClass = method.getDeclaringClass();
        if (interfaceClass != Object.class) {
            Object prevThis = Aspects.currentThis.get();
            try {
                Aspects.currentThis.set(proxy);
                MixinInstance interfaceInstance = getMixinForInterface(interfaceClass);
                return interfaceInstance.invokeMethod(proxy, method, args);
            } finally {
                Aspects.currentThis.set(prevThis);
            }
        } else {
            // for methods defined on Object:
            // change all proxies into AspectInstances and the call this aspect instance
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    Object arg = args[i];
                    if (Aspects.isAspectObject(arg)) {
                        args[i] = Aspects.getAspectInstance(arg);
                    }
                }
            }
            return method.invoke(this, args);
        }
    }

    Object getTarget(Class interfaceClass) {
        MixinInstance interfaceInstance = getMixinForInterface(interfaceClass);
        return interfaceInstance.getTarget();
    }

    Set getInterceptors(Class interfaceClass) {
        MixinInstance interfaceInstance = getMixinForInterface(interfaceClass);
        return interfaceInstance.getAllInterceptors();
    }

    private MixinInstance getMixinForInterface(Class interfaceClass) {
        MixinInstance mixinInstance = (MixinInstance) mixins.get(interfaceClass);
        assert mixinInstance != null : "there is not mixin for interface " + interfaceClass;
        return mixinInstance;
    }

    public void setTarget(Class interfaceClass, Object target) {
        MixinInstance mixinInstance = getMixinForInterface(interfaceClass);
        mixinInstance.setTarget(target);
    }

    public Object[] getTargets() {
        return CollectionUtils.collect(mixinsList, new Transformer() {
            public Object transform(Object o) {
                return ((MixinInstance) o).getTarget();
            }
        }).toArray();
    }

    private Object readResolve() {
        return Aspects.getAspectInstance(
                Aspects.getCurrentAspectFactory().newInstance(serializeClassIdentifier, serializeTargets));
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(getClassIdentifier());
        out.writeObject(getTargets());
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        serializeClassIdentifier = in.readObject();
        serializeTargets = (Object[]) in.readObject();
    }

    public Object getClassIdentifier() {
        return classIdentifier;
    }

    public void addMixin(MixinInstance mixinInstance) {
        assert proxy == null : "Can't addLink mixins when proxy has been created.";
        Class interfaceClass = mixinInstance.getInterfaceClass();
        bindMixinToInterface(interfaceClass, mixinInstance);
        mixinsList.add(mixinInstance);
    }

    /**
     * Binds the mixin to the specified interface and all of it's superclasses, overrides any other bindings
     * to that interface (and superclasses).
     * @param interfaceClass
     * @param mixinInstance
     */
    private void bindMixinToInterface(Class interfaceClass, MixinInstance mixinInstance) {
        mixins.put(interfaceClass, mixinInstance);
        Class superclass = interfaceClass.getSuperclass();
        if (superclass != null) {
            bindMixinToInterface(superclass, mixinInstance);
        }
        Class[] interfaces = interfaceClass.getInterfaces();
        if (interfaces != null) {
            for (int i = 0; i < interfaces.length; i++) {
                Class anInterface = interfaces[i];
                bindMixinToInterface(anInterface, mixinInstance);
            }
        }
    }

    public Set getAllInterceptors() {
        Set result = new HashSet();
        if (constructionInterceptors != null) {
            result.addAll(constructionInterceptors);
        }
        for (Iterator mixinIterator = mixins.values().iterator(); mixinIterator.hasNext();) {
            MixinInstance mixinInstance = (MixinInstance) mixinIterator.next();
            Set allInterceptors = mixinInstance.getAllInterceptors();
            for (Iterator interceptorIterator = allInterceptors.iterator(); interceptorIterator.hasNext();) {
                Interceptor interceptor = (Interceptor) interceptorIterator.next();
                result.add(interceptor);
            }
        }
        return result;
    }

    public List getInterceptorsForMethod(Method method) {
        return getMixinForInterface(method.getDeclaringClass()).getInterceptorsForMethod(method);
    }

    public AspectFactory getAspectFactory() {
        return aspectFactory;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AspectInstance)) return false;

        final AspectInstance aspectInstance = (AspectInstance) o;

        if (classIdentifier != null ? !classIdentifier.equals(aspectInstance.classIdentifier) : aspectInstance.classIdentifier != null) return false;
        if (!mixinsList.equals(aspectInstance.mixinsList)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = mixinsList.hashCode();
        result = 29 * result + (classIdentifier != null ? classIdentifier.hashCode() : 0);
        return result;
    }

    public class ConstructionInvocationImpl implements ConstructionInvocation {
        private Object proxy;
        private Class interfaceClass;

        public ConstructionInvocationImpl(Object proxy, Class interfaceClass) {
            this.proxy = proxy;
            this.interfaceClass = interfaceClass;
        }

        public Object getProxy() {
            return proxy;
        }

        public Object getTarget() {
            return Aspects.getTarget(proxy, interfaceClass);
        }

        public void setTarget(Object target) {
            Aspects.setTarget(proxy, interfaceClass, target);
        }
    }

    public Object getProxy() {
        Object proxy = createProxy();
        if (constructionInterceptors != null) {
            Object prevThis = Aspects.currentThis.get();
            try {
                Aspects.currentThis.set(proxy);
                for (Iterator iterator = constructionInterceptors.iterator(); iterator.hasNext();) {
                    ConstructionInterceptor constructionInterceptor = (ConstructionInterceptor) iterator.next();
                    Set interfaceClasses = getInterfaceClasses();
                    for (Iterator interfaceIterator = interfaceClasses.iterator(); interfaceIterator.hasNext();) {
                        Class interfaceClass = (Class) interfaceIterator.next();
                        if (constructionInterceptor.interceptsConstructor(interfaceClass)) {
                            proxy = constructionInterceptor.construct(new ConstructionInvocationImpl(proxy, interfaceClass));
                        }
                    }
                }
            } finally {
                constructionInterceptors = null;
                Aspects.currentThis.set(prevThis);
            }
        }
        return proxy;
    }

    public String toString() {
        if (mixinsList.size() == 1) {
            return "nanning{" + mixinsList.get(0).toString() + "}";
        }
        return "nanning{" + new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
                .append("class", classIdentifier)
                .append("mixins", mixinsList)
                .toString() + "}";
    }
}