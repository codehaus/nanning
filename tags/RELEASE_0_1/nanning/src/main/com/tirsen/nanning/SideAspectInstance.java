/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;


/**
 * TODO document AspectDefinition
 *
 * <!-- $Id: SideAspectInstance.java,v 1.9 2002-12-11 15:11:55 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.9 $
 */
class SideAspectInstance {
    private transient AspectDefinition aspectDefinition;
    private Class interfaceClass;
    private transient Interceptor[] interceptors;
    private Object target;
    private transient MethodInterceptor[][] methodInterceptors;

    public SideAspectInstance(AspectDefinition aspectDefinition) {
        this.aspectDefinition = aspectDefinition;
    }

    public void setInterface(Class interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public void setTarget(Object target) {
        aspectDefinition.checkTarget(target);
        this.target = target;
    }

    public Class getInterfaceClass() {
        return interfaceClass;
    }

    public Interceptor[] getAllInterceptors() {
        return interceptors;
    }

    void setInterceptors(InterceptorDefinition[] interceptorDefinitions, Interceptor[] interceptors)
            throws InstantiationException, IllegalAccessException {
        this.interceptors = interceptors;
        Method[] methods = aspectDefinition.getInterfaceClass().getMethods();
        methodInterceptors = new MethodInterceptor[methods.length][];
        List interceptorsForMethod = new ArrayList(methods.length);
        for (int methodIndex = 0; methodIndex < methods.length; methodIndex++) {
            Method method = methods[methodIndex];
            for (int interceptorIndex = 0; interceptorIndex < interceptors.length; interceptorIndex++) {
                Interceptor interceptor = interceptors[interceptorIndex];
                if (interceptorDefinitions[interceptorIndex].interceptsMethod(method)) {
                    interceptorsForMethod.add(interceptor);
                }
            }
            methodInterceptors[methodIndex] =
                    (MethodInterceptor[]) interceptorsForMethod.toArray(new MethodInterceptor[interceptorsForMethod.size()]);
            interceptorsForMethod.clear();
        }
    }

    public Object getTarget() {
        return target;
    }

    public MethodInterceptor[] getInterceptorsForMethod(Method method) {
        return methodInterceptors[aspectDefinition.getMethodIndex(method)];
    }

    public AspectDefinition getAspectDefinition() {
        return aspectDefinition;
    }

    public class ConstructionInvocationImpl implements ConstructionInvocation {
        private Object proxy;

        public ConstructionInvocationImpl(Object proxy) {
            this.proxy = proxy;
        }

        public Object getProxy() {
            return proxy;
        }

        public Object getTarget() {
            return SideAspectInstance.this.getTarget();
        }

        public void setTarget(Object target) {
            SideAspectInstance.this.setTarget(target);
        }
    }

    public Object invokeConstructor(Object proxy) {
        ConstructionInvocation constructionInvocation = null;
        for (int i = 0; i < interceptors.length; i++) {
            Interceptor interceptor = interceptors[i];
            if (interceptor instanceof ConstructionInterceptor) {
                InterceptorDefinition interceptorDefinition = aspectDefinition.getInterceptorDefinitions()[i];
                if (interceptorDefinition.interceptsConstructor(aspectDefinition.getInterfaceClass())) {
                    if (constructionInvocation == null) {
                        constructionInvocation = new ConstructionInvocationImpl(proxy);
                    }
                    return ((ConstructionInterceptor) interceptor).construct(constructionInvocation);
                }
            }
        }
        return proxy;
    }

    class InvocationImpl implements Invocation {
        private int index = -1;
        private Object proxy;
        private final Method method;
        private final Object[] args;

        public InvocationImpl(Object proxy, Method method, Object[] args) {
            this.proxy = proxy;
            this.method = method;
            this.args = args;
        }

        public Object invokeNext() throws Throwable {
            index++;
            MethodInterceptor[] interceptors = getInterceptorsForMethod(method);
            if (index < interceptors.length) {
                return interceptors[index].invoke(this);
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
            return getAllInterceptors()[index];
        }

        public Class getTargetInterface() {
            return getInterfaceClass();
        }

        public Object getTarget() {
            return SideAspectInstance.this.getTarget();
        }

        public void setTarget(Object target) {
            SideAspectInstance.this.setTarget(target);
        }

        public Object getProxy() {
            return proxy;
        }

        public int getCurrentIndex() {
            return index;
        }

        public int getNumberOfInterceptors() {
            return getAllInterceptors().length;
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
}
