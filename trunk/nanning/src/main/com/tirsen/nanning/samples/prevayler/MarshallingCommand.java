package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.ConstructionInvocation;

import java.io.Serializable;

import org.prevayler.PrevalentSystem;

/**
 * TODO document InvocationCommand
 *
 * @author <a href="mailto:jon_tirsen@yahoo.com">Jon Tirsén</a>
 * @version $Revision: 1.1 $
 */
public abstract class MarshallingCommand implements InvocationCommand {
    private Identity target;
    private Object[] args;

    public void setInvocation(Invocation invocation) {
        target = identify(invocation.getTarget());
        args = marshalArguments(invocation.getArgs());
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
        Object unmarshalledTarget = unmarshal(target);
        Object[] unmarshalledArgs = unmarshalArguments(args);
        return execute(system, unmarshalledTarget, unmarshalledArgs);
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
            throw new IllegalArgumentException("Can't unmarshal " + o);
        }
    }

    protected abstract Identity identify(Object target);

    protected abstract Object resolve(Identity identity);

    protected abstract Serializable execute(
            PrevalentSystem system, Object unmarshalledTarget, Object[] unmarshalledArgs);
}
