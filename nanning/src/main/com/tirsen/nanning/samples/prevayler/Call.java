package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.Invocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.lang.reflect.Method;

public class Call implements Serializable {
    static final long serialVersionUID = -3336463259251779539L;
    
    private static final Log logger = LogFactory.getLog(Call.class);

    protected Object target;
    protected Object[] args;
    private Object classIdentifier;
    private Class interfaceClass;
    private Class[] parameterTypes;
    private String methodName;

    public Call(Invocation invocation) {
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
            throw new RuntimeException("did not find method " + methodName + "returning null", e);
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
}
