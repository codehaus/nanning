package com.tirsen.nanning.samples.prevayler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.prevayler.PrevalentSystem;
import org.prevayler.Command;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.tirsen.nanning.Invocation;

public class InvokeCommand implements Command {
    private static final Log logger = LogFactory.getLog(InvokeCommand.class);
    private Identity target;
    private Object[] args;
    private Class declaringClass;
    private Class[] parameterTypes;
    private String methodName;

    public InvokeCommand(Invocation invocation) {
        declaringClass = invocation.getMethod().getDeclaringClass();
        methodName = invocation.getMethod().getName();
        parameterTypes = invocation.getMethod().getParameterTypes();
        target = identify(invocation.getProxy());
        args = marshalArguments(invocation.getArgs());
    }

    protected Identity identify(Object object) {
        if(object instanceof MyObject) {
            return new Identity(MyObject.class, new Integer(CurrentPrevayler.getSystem().getOID(object)));
        }
        throw new IllegalArgumentException("Can't identify " + object);
    }

    protected Object resolve(Identity identity) {
        if(MyObject.class.isAssignableFrom(identity.getObjectClass())) {
            return CurrentPrevayler.getSystem().getObjectWithID(((Integer) identity.getIdentifier()).intValue());
        }
        throw new IllegalArgumentException("Can't resolve " + identity.getObjectClass());
    }

    protected Serializable execute(
            PrevalentSystem system, Method method, Object unmarshalledTarget, Object[] unmarshalledArgs) {
        CurrentPrevayler.getPrevaylerInterceptor().enterTransaction();
        CurrentPrevayler.setSystem((IdentifyingSystem) system);
        try {
            try {
                return (Serializable) method.invoke(unmarshalledTarget, unmarshalledArgs);
            } catch (Exception e) {
                logger.fatal("Failed to execute command.", e);
                throw new RuntimeException(e);
            }
        } finally {
            CurrentPrevayler.getPrevaylerInterceptor().exitTransaction();
        }
    }

    private Object[] marshalArguments(Object[] args) {
        if(args == null) {
            return null;
        }

        Object[] marshalled = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            marshalled[i] = marshal(arg);
        }
        return marshalled;
    }

    protected Object marshal(Object o) {
        if (Number.class.isInstance(o)) {
            return o;
        } else if (String.class.isInstance(o)) {
            return o;
        } else if (Character.class.isInstance(o)) {
            return o;
        } else {
            return identify(target);
        }
    }

    public Serializable execute(PrevalentSystem system) throws Exception {
        Object unmarshalledTarget = resolve(target);
        Object[] unmarshalledArgs = unmarshalArguments(args);
        Method method = declaringClass.getMethod(methodName, parameterTypes);
        return execute(system, method, unmarshalledTarget, unmarshalledArgs);
    }

    private Object[] unmarshalArguments(Object[] args) {
        if(args == null) {
            return null;
        }

        Object[] unmarshalled = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            Object o = args[i];
            unmarshalled[i] = unmarshal(o);
        }
        return unmarshalled;
    }

    private Object unmarshal(Object o) {
        if (Number.class.isInstance(o)) {
            return o;
        } else if (String.class.isInstance(o)) {
            return o;
        } else if (Character.class.isInstance(o)) {
            return o;
        } else if(o instanceof Identity) {
            return resolve(target);
        } else {
            throw new IllegalArgumentException("Can't resolve " + o);
        }
    }
}
