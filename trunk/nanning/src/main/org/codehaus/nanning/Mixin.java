/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.codehaus.nanning;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * A mixin consists of an interface, a target and a number of interceptors intercepting calls to methods, a mixin
 * can also be a concrete class (no interface/implementation-separation) but at most one mixins with conrete classes
 * per AspectInstance is supported.
 * Create a mixin with interface and target:
 * <pre><code>
new MixinInstance(Interface.class, new Target());
</code></pre>
 * Create a mixin with a concrete class:
 * <pre><code>
new MixinInstance(Target.class);
</code></pre>
 * Add interceptor to method:
 * <pre><code>
Method method = Target.class.getMethod("method", null);
MixinInstance mixin = new MixinInstance(Target.class);
mixin.addInterceptor(method, new MethodInterceptor() {
    public Object invoke(Invocation invocation) {
        return invocation.invokeNext();
    }
}
</code></pre>
 *
 * <!-- $Id: Mixin.java,v 1.1 2003-07-12 16:48:16 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.1 $
 */
public class Mixin implements Serializable {
    static final long serialVersionUID = 7386027290257587762L;

    private Class interfaceClass;
    private Object target;

    private transient Map methodInterceptors = new HashMap();

    public Mixin() {
    }

    public Mixin(Class interfaceClass, Object target) {
        setInterfaceClass(interfaceClass);
        setTarget(target);
    }

    public void setInterfaceClass(Class interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public void setTarget(Object target) {
        assert !(target instanceof Mixin);
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
        protected ListIterator interceptors;

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

        public int getArgumentCount() {
            return args.length;
        }

        public Object getArgument(int arg) {
            return args[arg];
        }

        public Object getTarget() {
            return Mixin.this.target;
        }

        public void setTarget(Object target) {
            Mixin.this.setTarget(target);
        }

        public Object getProxy() {
            return proxy;
        }

        public int getCurrentIndex() {
            return interceptors.previousIndex();
        }

        public int getfInterceptorCount() {
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

    public Object invokeMethod(Object proxy, Method method, Object[] args)
            throws Throwable {
        Invocation invocation = new InvocationImpl(proxy, method, args);
        Object returnValue = invocation.invokeNext();
        return returnValue;
    }

    public boolean isMainMixin() {
        return !getInterfaceClass().isInterface();
    }

    /**
     * Add interceptor to all methods of the mixin.
     * @param interceptor
     */
    public void addInterceptor(MethodInterceptor interceptor) {
        Method[] methods = getAllMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            addInterceptor(method, interceptor);
        }
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mixin)) return false;

        final Mixin mixinInstance = (Mixin) o;

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
     * Add interceptor to specified method.
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
