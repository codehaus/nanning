/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


/**
 * TODO document AspectDefinition
 *
 * <!-- $Id: MixinInstance.java,v 1.9 2003-03-21 17:11:10 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.9 $
 */
public final class MixinInstance {
    private Class interfaceClass;
    private Object target;
    private Map methodInterceptors = new HashMap();

    public MixinInstance() {
    }

    public void setInterfaceClass(Class interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Class getInterfaceClass() {
        return interfaceClass;
    }

    public Set getAllInterceptors() {
        Set allInterceptors = new HashSet();
        for (Iterator methodIterator = methodInterceptors.values().iterator(); methodIterator.hasNext();) {
            List interceptors = (List) methodIterator.next();
            for (Iterator interceptorIterator = interceptors.iterator(); interceptorIterator.hasNext();) {
                Interceptor interceptor = (Interceptor) interceptorIterator.next();
                allInterceptors.add(interceptor);
            }
        }
        return allInterceptors;
    }

    public Object getTarget() {
        return target;
    }

    public List getInterceptorsForMethod(Method method) {
        List interceptors = (List) methodInterceptors.get(method);
        if (interceptors == null) {
            interceptors = new ArrayList();
            methodInterceptors.put(method, interceptors);
        }
        return interceptors;
    }

    class InvocationImpl implements Invocation {
        private Object proxy;
        private final Method method;
        private final Object[] args;
        private ListIterator interceptors;

        public InvocationImpl(Object proxy, Method method, Object[] args) {
            this.proxy = proxy;
            this.method = method;
            this.args = args;
            interceptors = getInterceptorsForMethod(method).listIterator();
        }

        public Object invokeNext() throws Throwable {
            if (interceptors.hasNext()) {
                return ((MethodInterceptor) interceptors.next()).invoke(this);
            } else {
                try {
                    return method.invoke(getTarget(), args);
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
            return (Interceptor) getInterceptorsForMethod(method).get(index);
        }

        public Class getTargetInterface() {
            return getInterfaceClass();
        }

        public AspectInstance getAspectInstance() {
            return Aspects.getAspectInstance(getProxy());
        }

        public Object getTarget() {
            return MixinInstance.this.getTarget();
        }

        public void setTarget(Object target) {
            MixinInstance.this.setTarget(target);
        }

        public Object getProxy() {
            return proxy;
        }

        public int getCurrentIndex() {
            return interceptors.previousIndex();
        }

        public int getNumberOfInterceptors() {
            return getInterceptorsForMethod(method).size();
        }

        public Method getMethod() {
            return method;
        }

        public Object[] getArgs() {
            return args;
        }
    }

    public Object invokeMethod(Object proxy,
                               Method method,
                               Object[] args)
            throws Throwable {
        Invocation invocation = new InvocationImpl(proxy, method, args);
        return invocation.invokeNext();
    }

    /**
     * Adds this interceptor to all methods.
     * @param interceptor
     */
    public void addInterceptor(Interceptor interceptor) {
        Method[] methods = getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (interceptor instanceof MethodInterceptor) {
                addInterceptor(method, (MethodInterceptor) interceptor);
            }
        }
    }

    /**
     * Adds interceptor to specified method.
     * @param method
     * @param interceptor
     */
    public void addInterceptor(Method method, MethodInterceptor interceptor) {
        getInterceptorsForMethod(method).add(interceptor);
    }

    public Method[] getMethods() {
        return interfaceClass.getMethods();
    }

    public String toString() {
        return "mixin{" + getTarget().toString() + "}";
    }
}
