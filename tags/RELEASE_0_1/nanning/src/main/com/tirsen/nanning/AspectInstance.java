/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * TODO document AspectInstance
 *
 * <!-- $Id: AspectInstance.java,v 1.19 2002-12-12 08:27:57 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.19 $
 */
class AspectInstance implements InvocationHandler, Externalizable {
    private static final Method OBJECT_EQUALS_METHOD;

    private Object proxy;
    private SideAspectInstance[] sideAspectInstances;
    private AspectClass aspectClass;

    /**
     * Used during serialization only.
     */
    private Class serializeInterfaceClass;
    /**
     * Used during serialization only.
     */
    private Object[] serializeTargets;


    static {
        try {
            OBJECT_EQUALS_METHOD = Object.class.getMethod("equals", new Class[]{Object.class});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static ThreadLocal currentThis = new ThreadLocal();

    public AspectInstance() {
    }

    public AspectInstance(AspectClass aspectClass, SideAspectInstance[] sideAspectInstances) {
        this.sideAspectInstances = sideAspectInstances;
        this.aspectClass = aspectClass;
    }

    Object init(Object proxy) {
        return this.proxy = invokeConstructor(proxy);
    }

    private Object invokeConstructor(Object proxy) {
        for (int i = 0; i < sideAspectInstances.length; i++) {
            SideAspectInstance sideAspectInstance = sideAspectInstances[i];
            proxy = sideAspectInstance.invokeConstructor(proxy);
        }
        return proxy;
    }

    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        Class interfaceClass = method.getDeclaringClass();
        if (interfaceClass != Object.class) {
            Object prevThis = currentThis.get();
            try {
                currentThis.set(proxy);
                SideAspectInstance interfaceInstance = getSideAspectInstance(interfaceClass);
                // if it wasn't defined by any of the specified interfaces let's assume it's the default one (ie. index 0)

                return interfaceInstance.invokeMethod(proxy, method, args);
            } finally {
                currentThis.set(prevThis);
            }
        } else {
            if (OBJECT_EQUALS_METHOD.equals(method) && Aspects.isAspectObject(args[0])) {
                args[0] = Aspects.getClassTarget(args[0]);
            }
            // main-target take care of all calls to Object (such as equals, toString and so on)
            return method.invoke(sideAspectInstances[0].getTarget(), args);
        }
    }

    Object getTarget(Class interfaceClass) {
        SideAspectInstance interfaceInstance = getSideAspectInstance(interfaceClass);
        return interfaceInstance.getTarget();
    }

    Interceptor[] getClassInterceptors() {
        // the actual class-specific interface-instance is at the first position
        return sideAspectInstances[0].getAllInterceptors();
    }

    Interceptor[] getInterceptors(Class interfaceClass) {
        SideAspectInstance interfaceInstance = getSideAspectInstance(interfaceClass);
        return interfaceInstance.getAllInterceptors();
    }

    private SideAspectInstance getSideAspectInstance(Class interfaceClass) {
        return sideAspectInstances[aspectClass.getSideAspectIndexForInterface(interfaceClass)];
    }

    public void setTarget(Class interfaceClass, Object target) {
        SideAspectInstance sideAspectInstance = getSideAspectInstance(interfaceClass);
        sideAspectInstance.setTarget(target);
    }

    public String toString() {
        SideAspectInstance defaultInterfaceInstance = sideAspectInstances[0];
        return new ToStringBuilder(this)
                .append("interface", defaultInterfaceInstance.getInterfaceClass().getName())
                .append("target", defaultInterfaceInstance.getTarget())
                .toString();
    }

    public AspectClass getAspectClass() {
        return aspectClass;
    }

    public Object[] getTargets() {
        Object[] targets = new Object[sideAspectInstances.length];
        for (int i = 0; i < sideAspectInstances.length; i++) {
            SideAspectInstance sideAspectInstance = sideAspectInstances[i];
            targets[i] = sideAspectInstance.getTarget();
        }
        return targets;
    }

    private Object readResolve() {
        return Aspects.getAspectInstance(
                Aspects.getCurrentAspectRepository().newInstance(serializeInterfaceClass, serializeTargets));
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(sideAspectInstances[0].getInterfaceClass());
        out.writeObject(getTargets());
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        serializeInterfaceClass = (Class) in.readObject();
        serializeTargets = (Object[]) in.readObject();
    }
}
