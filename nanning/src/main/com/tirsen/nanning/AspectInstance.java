/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * TODO document AspectInstance
 *
 * <!-- $Id: AspectInstance.java,v 1.14 2002-11-25 12:17:07 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.14 $
 */
class AspectInstance implements InvocationHandler {
    private static final Method OBJECT_EQUALS_METHOD;

    static {
        try {
            OBJECT_EQUALS_METHOD = Object.class.getMethod("equals", new Class[]{Object.class});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static ThreadLocal currentThis = new ThreadLocal();

    class InvocationImpl implements Invocation {
        private int index = -1;
        private final Method method;
        private final Object[] args;
        private final SideAspectInstance sideAspectInstance;

        public InvocationImpl(Method method, Object[] args, SideAspectInstance interfaceInstance) {
            this.method = method;
            this.args = args;
            this.sideAspectInstance = interfaceInstance;
        }

        public Object invokeNext() throws Throwable {
            index++;
            Interceptor[] interceptors = sideAspectInstance.getInterceptorsForMethod(method);
            if (index < interceptors.length) {
                return interceptors[index].invoke(this);
            } else {
                try {
                    return method.invoke(sideAspectInstance.getTarget(), args);
                } catch (InvocationTargetException e) {
                    throwRealException(e);
                    throw e;
                }
            }
        }

        private void throwRealException(InvocationTargetException e) throws Exception {
            Throwable realException = e.getTargetException();
            if (realException instanceof Error) {
                throw (Error) realException;
            } else if (realException instanceof RuntimeException) {
                throw (RuntimeException) realException;
            } else {
                throw (Exception) realException;
            }
        }

        public Interceptor getInterceptor(int index) {
            return sideAspectInstance.getAllInterceptors()[index];
        }

        public Object getTarget() {
            return sideAspectInstance.getTarget();
        }

        public void setTarget(Object target) {
            sideAspectInstance.setTarget(target);
        }

        public Object getProxy() {
            return proxy;
        }

        public int getCurrentIndex() {
            return index;
        }

        public int getNumberOfInterceptors() {
            return sideAspectInstance.getAllInterceptors().length;
        }

        public Method getMethod() {
            return method;
        }

        public Object[] getArgs() {
            return args;
        }
    }

    private Object proxy;
    private final SideAspectInstance[] sideAspectInstances;
    private final AspectClass aspectClass;

    public AspectInstance(AspectClass aspectClass, SideAspectInstance[] sideAspectInstances) {
        this.proxy = proxy;
        this.sideAspectInstances = sideAspectInstances;
        this.aspectClass = aspectClass;
    }

    void setProxy(Object proxy) {
        this.proxy = proxy;
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

                Invocation invocation = new InvocationImpl(method, args, interfaceInstance);
                return invocation.invokeNext();
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
}
