package com.tirsen.nanning.prevayler;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.tirsen.nanning.Invocation;

public class Call implements Serializable {
    static final long serialVersionUID = -3336463259251779539L;

    protected Object target;
    protected Object[] args;
    private Object classIdentifier;
    private Class interfaceClass;
    private Class[] parameterTypes;
    private String methodName;

    public Call(Invocation invocation) throws Exception {
        setInvocation(invocation);
    }

    public Call() {
    }

    protected void setInvocation(Invocation invocation) throws Exception {
        classIdentifier = invocation.getAspectInstance().getClassIdentifier();
        interfaceClass = invocation.getTargetInterface();
        methodName = invocation.getMethod().getName();
        parameterTypes = invocation.getMethod().getParameterTypes();
        target = invocation.getProxy();
        args = invocation.getArgs();
    }

    public Method getMethod() {
        try {
            return interfaceClass.getMethod(methodName, parameterTypes);
        } catch (Exception e) {
            throw new RuntimeException("did not find method " + methodName + " returning null", e);
        }
    }

    public Class getInterfaceClass() {
        return interfaceClass;
    }

    public Object getClassIdentifier() {
        return classIdentifier;
    }

    public Object[] getArgs() {
        return args;
    }

    public Object getTarget() {
        return target;
    }

    public Object invoke() throws Exception {
        final Method method = getMethod();
        final Object target = getTarget();
        final Object[] args = getArgs();

        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof Exception) {
                throw (Exception) e.getTargetException();
            } else if (e.getTargetException() instanceof Error) {
                throw (Error) e.getTargetException();
            } else {
                throw e;
            }
        }
    }
}
