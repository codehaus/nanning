package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.Invocation;
import org.prevayler.PrevalentSystem;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * TODO document MarshallingCommand
 *
 * @author <a href="mailto:jon_tirsen@yahoo.com">Jon Tirsén</a>
 * @version $Revision: 1.3 $
 */
public abstract class MarshallingCommand implements InvocationCommand {
    static final long serialVersionUID = 5848314669553768335L;

    private Identity target;
    private Object[] args;
    private Class declaringClass;
    private Class[] parameterTypes;
    private String methodName;

    public void setInvocation(Invocation invocation) {
        declaringClass = invocation.getMethod().getDeclaringClass();
        methodName = invocation.getMethod().getName();
        parameterTypes = invocation.getMethod().getParameterTypes();
        target = identify(invocation.getProxy());
        args = marshalArguments(invocation.getArgs());
    }

    private Object[] marshalArguments(Object[] args) {
        if (args == null) {
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
        if (args == null) {
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
        } else if (o instanceof Identity) {
            return resolve(target);
        } else {
            throw new IllegalArgumentException("Can't resolve " + o);
        }
    }

    protected abstract Identity identify(Object object);

    protected abstract Object resolve(Identity identity);

    protected abstract Serializable execute(
            PrevalentSystem system, Method method, Object unmarshalledTarget, Object[] unmarshalledArgs);
}
