/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 * (C) 2003 Jon Tirsen
 */
package com.tirsen.nanning;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * The central concept of the Nanning Core, contains mixins.
 * Use like this:
 * <pre><code>
        AspectInstance aspectInstance = new AspectInstance();
        MixinInstance mixinInstance = new MixinInstance();
        mixinInstance.setInterfaceClass(Intf.class);
        mixinInstance.addInterceptor(new MockInterceptor());
        mixinInstance.addInterceptor(new NullInterceptor());
        mixinInstance.setTarget(new Impl());
        aspectInstance.addMixin(mixinInstance);
</pre></code>
 *
 * <!-- $Id: AspectInstance.java,v 1.34 2003-04-16 13:56:00 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.34 $
 */
public final class AspectInstance implements InvocationHandler, Externalizable {
    static final long serialVersionUID = 5462785783512485056L;

    private Object proxy;
    private Map mixins = new HashMap();

    /**
     * Used during serialization only.
     */
    private Class serializeClassIdentifier;
    /**
     * Used during serialization only.
     */
    private Object[] serializeTargets;


    private List mixinsList = new ArrayList();
    private AspectFactory aspectFactory;
    private Class classIdentifier;
    private List constructionInterceptors;

    public AspectInstance() {
    }

    public AspectInstance(AspectFactory aspectFactory, Class classIdentifier) {
        this.aspectFactory = aspectFactory;
        this.classIdentifier = classIdentifier;
    }

    public AspectInstance(Class classIdentifier) {
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

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

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

    /**
     * Returns the mixin with the specified interface.
     * @param interfaceClass
     * @return
     */
    public MixinInstance getMixinForInterface(Class interfaceClass) {
        MixinInstance mixinInstance = (MixinInstance) mixins.get(interfaceClass);
        assert mixinInstance != null : "there is no mixin for interface " + interfaceClass;
        return mixinInstance;
    }

    public boolean hasMixinForInterface(Class interfaceClass) {
        return mixins.containsKey(interfaceClass);
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
        AspectFactory currentAspectFactory = Aspects.getCurrentAspectFactory();
        assert currentAspectFactory != null : "context AspectFactory not specified, it is not possible to deserialize " + this;
        assert serializeClassIdentifier != null && serializeTargets != null;
        AspectInstance aspectInstance = Aspects.getAspectInstance(currentAspectFactory.newInstance(serializeClassIdentifier, serializeTargets));
        assert aspectInstance != null;

        return aspectInstance;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(getClassIdentifier());
        out.writeObject(getTargets());
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        serializeClassIdentifier = (Class) in.readObject();
        serializeTargets = (Object[]) in.readObject();
    }

    public Class getClassIdentifier() {
        return classIdentifier;
    }

    /**
     * Adds a mixin.
     * @param mixinInstance
     */
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

    /**
     * Returns all the interceptors referenced by this aspect instance. That is all interceptors of all methods of
     * all mixins, invluding the construction-interceptors.
     * @return
     */
    public Set getAllInterceptors() {
        Set result = new LinkedHashSet();
        if (constructionInterceptors != null) {
            result.addAll(constructionInterceptors);
        }
        for (Iterator mixinIterator = mixinsList.iterator(); mixinIterator.hasNext();) {
            MixinInstance mixinInstance = (MixinInstance) mixinIterator.next();
            Set allInterceptors = mixinInstance.getAllInterceptors();
            for (Iterator interceptorIterator = allInterceptors.iterator(); interceptorIterator.hasNext();) {
                Interceptor interceptor = (Interceptor) interceptorIterator.next();
                result.add(interceptor);
            }
        }
        return result;
    }

    /**
     * Returns the interceptors of the specified method, searches in the mixin for the interface that the method
     * has been declared on.
     * @param method
     * @return
     */
    public List getInterceptorsForMethod(Method method) {
        return getMixinForInterface(method.getDeclaringClass()).getInterceptorsForMethod(method);
    }

    /**
     * Returns the AspectFactory used to create and configure this AspectInstance (if set by the AspectFactory).
     * @return
     */
    public final AspectFactory getAspectFactory() {
        return aspectFactory;
    }

    public final class ConstructionInvocationImpl implements ConstructionInvocation {
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
            return "aspect{" + mixinsList.get(0).toString() + "}";
        }
        return "aspect{" + new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
                .append("class", classIdentifier)
                .append("mixins", mixinsList)
                .toString() + "}";
    }

    /**
     * Returns all mixins defined on this AspectInstance.
     * @return
     */
    public Collection getMixins() {
        return Collections.unmodifiableCollection(mixinsList);
    }

    /**
     * Adds a ConstructionInterceptor, the interceptor will be invoked when creating the proxy in {@link #getProxy()}.
     *
     * @param constructionInterceptor
     */
    public void addConstructionInterceptor(ConstructionInterceptor constructionInterceptor) {
        if (constructionInterceptors == null) {
            constructionInterceptors = new ArrayList();
        }
        constructionInterceptors.add(constructionInterceptor);
    }
}