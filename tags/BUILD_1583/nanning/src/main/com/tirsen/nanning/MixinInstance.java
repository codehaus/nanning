/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


/**
 * TODO document AspectDefinition
 *
 * <!-- $Id: MixinInstance.java,v 1.15 2003-06-11 15:13:32 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.15 $
 */
public final class MixinInstance implements Serializable {
    static final long serialVersionUID = 7386027290257587762L;

    private Class interfaceClass;
    private Object target;

    private transient Map methodInterceptors = new HashMap();

    public MixinInstance() {
    }

    public MixinInstance(Class interfaceClass, Object target) {
        setInterfaceClass(interfaceClass);
        setTarget(target);
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
        if (methodInterceptors != null) {
            for (Iterator methodIterator = methodInterceptors.values().iterator(); methodIterator.hasNext();) {
                List interceptors = (List) methodIterator.next();
                for (Iterator interceptorIterator = interceptors.iterator(); interceptorIterator.hasNext();) {
                    Interceptor interceptor = (Interceptor) interceptorIterator.next();
                    allInterceptors.add(interceptor);
                }
            }
        }
        return allInterceptors;
    }

    public Object getTarget() {
        return target;
    }

    public List getInterceptorsForMethod(Method method) {
        if (methodInterceptors == null) {
            methodInterceptors = new HashMap();
        }
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

        public Object getArg(int arg) {
            return args[arg];
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
        assert !(interceptor instanceof ConstructionInterceptor) : "Construction interceptors are added on the aspect instance";
        Method[] methods = getAllMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (interceptor instanceof FilterMethodsInterceptor) {
                FilterMethodsInterceptor filterMethodsInterceptor = (FilterMethodsInterceptor) interceptor;
                if (filterMethodsInterceptor.interceptsMethod(method)) {
                    addInterceptor(method, (MethodInterceptor) interceptor);
                }
            } else {
                addInterceptor(method, (MethodInterceptor) interceptor);
            }
        }
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MixinInstance)) return false;

        final MixinInstance mixinInstance = (MixinInstance) o;

        if (interfaceClass != null ? !interfaceClass.equals(mixinInstance.interfaceClass) : mixinInstance.interfaceClass != null) return false;
        if (target != null ? !target.equals(mixinInstance.target) : mixinInstance.target != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (interfaceClass != null ? interfaceClass.hashCode() : 0);
        result = 29 * result + (target != null ? target.hashCode() : 0);
        return result;
    }

    /**
     * Adds interceptor to specified method.
     * @param method
     * @param interceptor
     */
    public void addInterceptor(Method method, MethodInterceptor interceptor) {
        getInterceptorsForMethod(method).add(interceptor);
    }

    public Method[] getAllMethods() {
        return interfaceClass.getMethods();
    }

    public String toString() {
        return "mixin{" + getTarget() + "}";
    }
}
