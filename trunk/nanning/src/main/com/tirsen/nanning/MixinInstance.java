/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import net.sf.cglib.MethodProxy;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


/**
 * TODO document AspectDefinition
 *
 * <!-- $Id: MixinInstance.java,v 1.16 2003-06-22 16:26:26 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.16 $
 */
public class MixinInstance implements Serializable {
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

    public MixinInstance(Class aClass) {
        setInterfaceClass(aClass);
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

    protected class InvocationImpl implements Invocation {
        protected Object proxy;
        protected final Method method;
        protected final Object[] args;
        private MethodProxy methodProxy;
        protected ListIterator interceptors;

        public InvocationImpl(Object proxy, Method method, Object[] args, MethodProxy methodProxy) {
            this.proxy = proxy;
            this.method = method;
            this.args = args;
            this.methodProxy = methodProxy;
            interceptors = getInterceptorsForMethod(method).listIterator();
        }

        public Object invokeNext() throws Throwable {
            if (interceptors.hasNext()) {
                return ((MethodInterceptor) interceptors.next()).invoke(this);
            } else {
                try {
                    if (methodProxy == null) {
                        return method.invoke(getTarget(), args);
                    } else {
                        return methodProxy.invokeSuper(proxy, args);
                    }
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
            return MixinInstance.this.target;
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

    public Class getMainClass() {
        return getInterfaceClass();
    }

    public Object invokeMethod(Object proxy, Method method, Object[] args, MethodProxy methodProxy)
            throws Throwable {
        assert isMainMixin() ? methodProxy != null : true : "sorry, I need to be invoked with a MethodProxy";

        Invocation invocation = new InvocationImpl(proxy, method, args, methodProxy);
        Object returnValue = invocation.invokeNext();
        return returnValue;
    }

    public boolean isMainMixin() {
        return !getInterfaceClass().isInterface();
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
