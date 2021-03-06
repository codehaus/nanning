/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 * (C) 2003 Jon Tirsen
 */
package org.codehaus.nanning;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

import org.codehaus.nanning.util.LinkedHashSet;

/**
 * The central concept of the Nanning Core, contains mixins.
 * Use like this:
 * <pre><code>
 AspectInstance instance = new AspectInstance();
 Mixin mixin = new Mixin();
 mixin.setInterfaceClass(Intf.class);
 mixin.addInterceptor(new MockInterceptor());
 mixin.addInterceptor(new NullInterceptor());
 mixin.setTarget(new Impl());
 instance.addMixin(mixin);
 </pre></code>
 *
 * <!-- $Id: AspectInstance.java,v 1.4 2003-09-05 07:56:43 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.4 $
 */
public final class AspectInstance implements InvocationHandler, Serializable {
    static final long serialVersionUID = 5462785783512485056L;

    private Map mixins = new HashMap(1);
    private List mixinsList = new ArrayList(1);
    private Class classIdentifier;

    private Object proxy;
    private transient List constructionInterceptors = new ArrayList(1);
    private transient AspectFactory aspectFactory;

    public AspectInstance() {
    }

    public AspectInstance(AspectFactory aspectFactory, Class classIdentifier) {
        this.aspectFactory = aspectFactory;
        this.classIdentifier = classIdentifier;
    }

    public AspectInstance(Class classIdentifier) {
        this.classIdentifier = classIdentifier;
    }

    private Set getInterfaceClasses() {
        Set interfaces = new HashSet();
        for (Iterator iterator = mixinsList.iterator(); iterator.hasNext();) {
            Mixin mixinInstance = (Mixin) iterator.next();
            interfaces.add(mixinInstance.getInterfaceClass());
        }
        if (classIdentifier != null) {
            interfaces.add(classIdentifier);
        }
        return interfaces;
    }

    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        Class interfaceClass = method.getDeclaringClass();

        if (interfaceClass != Object.class) {
            Object prevThis = Aspects.getThis();
            try {
                Aspects.setThis(proxy);
                Mixin mixin = getMixinForInterface(interfaceClass);
                return mixin.invokeMethod(proxy, method, args);
            } finally {
                Aspects.setThis(prevThis);
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
        Mixin interfaceInstance = getMixinForInterface(interfaceClass);
        return interfaceInstance.getTarget();
    }

    Set getInterceptors(Class interfaceClass) {
        Mixin interfaceInstance = getMixinForInterface(interfaceClass);
        return interfaceInstance.getAllInterceptors();
    }

    /**
     * Returns the mixin with the specified interface.
     * @param interfaceClass
     * @return
     */
    public Mixin getMixinForInterface(Class interfaceClass) {
        Mixin mixinInstance = (Mixin) mixins.get(interfaceClass);
        if (mixinInstance == null) {
            throw new AssertionException("there is no mixin for interface " + interfaceClass + " mixins were " + mixins);
        }
        return mixinInstance;
    }

    public boolean hasMixinForInterface(Class interfaceClass) {
        return mixins.containsKey(interfaceClass);
    }

    public void setTarget(Class interfaceClass, Object target) {
        Mixin mixinInstance = getMixinForInterface(interfaceClass);
        mixinInstance.setTarget(target);
    }

    public Object[] getTargets() {
        Object[] targets = new Object[mixinsList.size()];
        for (int i = 0; i < targets.length; i++) {
            targets[i] = ((Mixin) mixinsList.get(i)).getTarget();
        }
        return targets;
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        AspectFactory currentAspectFactory = Aspects.getCurrentAspectFactory();
        if (currentAspectFactory == null) {
            throw new AssertionException("context AspectFactory not specified, it is not possible to deserialize " + this);
        }
        aspectFactory = currentAspectFactory;
        aspectFactory.reinitialize(this);
    }

    public Class getClassIdentifier() {
        return classIdentifier;
    }

    /**
     * Adds a mixin.
     * @param mixin
     */
    public void addMixin(Mixin mixin) {
        if (proxy != null) {
            throw new AssertionException("Can't add mixins when proxy has been created.");
        }
        Class interfaceClass = mixin.getInterfaceClass();
        bindMixinToInterface(interfaceClass, mixin);
        mixinsList.add(mixin);
    }

    public void setMixins(List mixinsList) {
        this.mixinsList = mixinsList;
        mixins.clear();
        for (Iterator i = mixinsList.iterator(); i.hasNext();) {
            Mixin mixinInstance = (Mixin) i.next();
            bindMixinToInterface(mixinInstance.getInterfaceClass(), mixinInstance);
        }
    }

    /**
     * Binds the mixin to the specified interface and all of it's superclasses, overrides any other bindings
     * to that interface (and superclasses).
     * @param interfaceClass
     * @param mixinInstance
     */
    private void bindMixinToInterface(Class interfaceClass, Mixin mixinInstance) {
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
            Mixin mixinInstance = (Mixin) mixinIterator.next();
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
        if (proxy == null) {
            Set interfaces = getInterfaceClasses();
            proxy = Proxy.newProxyInstance(getClass().getClassLoader(),
                                           (Class[]) interfaces.toArray(new Class[0]),
                    this);
        }
        proxy = executeConstructionInterceptors(proxy);
        return proxy;
    }

    private Object executeConstructionInterceptors(Object proxy) {
        Object prevThis = Aspects.getThis();
        try {
            Aspects.setThis(proxy);

            if (constructionInterceptors != null) {
                for (Iterator iterator = constructionInterceptors.iterator(); iterator.hasNext();) {
                    ConstructionInterceptor constructionInterceptor = (ConstructionInterceptor) iterator.next();
                    proxy = constructionInterceptor.construct(new ConstructionInvocationImpl(proxy, getClassIdentifier()));
                }
            }

        } finally {
            constructionInterceptors = null;
            Aspects.setThis(prevThis);
        }
        return proxy;
    }

    public String toString() {
        if (mixinsList.size() == 1) {
            return "aspect{" + mixinsList.get(0).toString() + "}";
        }
        return "aspect{class=" + classIdentifier + "," +
                "mixins=" + mixinsList + "}";
    }

    /**
     * Returns all mixins defined on this AspectInstance.
     * @return
     */
    public List getMixins() {
        return Collections.unmodifiableList(mixinsList);
    }

    /**
     * Adds a ConstructionInterceptor, the interceptor will be invoked when creating the proxy in {@link #getProxy()}.
     */
    public void addConstructionInterceptor(ConstructionInterceptor constructionInterceptor) {
        if (constructionInterceptors == null) {
            constructionInterceptors = new ArrayList(1);
        }
        constructionInterceptors.add(constructionInterceptor);
    }

    public void addInterceptor(MethodInterceptor interceptor) {
        for (Iterator iterator = mixinsList.iterator(); iterator.hasNext();) {
            Mixin mixin = (Mixin) iterator.next();
            mixin.addInterceptor(interceptor);
        }
    }
}
