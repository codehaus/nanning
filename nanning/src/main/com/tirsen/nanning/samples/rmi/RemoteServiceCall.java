package com.tirsen.nanning.samples.rmi;

import com.tirsen.nanning.samples.prevayler.Call;
import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.SerializableAspectInstance;

public class RemoteServiceCall extends Call {
    public RemoteServiceCall(Invocation invocation) {
        super(invocation);
        args = marshalArgs(args);
        target = null;
    }

    private Object[] marshalArgs(Object[] args) {
        if(args == null) {
            return null;
        }

        Object[] marshalledArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            marshalledArgs[i] = marshal(arg);
        }

        return marshalledArgs;
    }

    public static Object marshal(Object object) {
        if(Aspects.isAspectObject(object)) {
            object = new SerializableAspectInstance(object);
        }
        return object;
    }

    public Object getTarget() {
        return Aspects.getCurrentAspectRepository().newInstance(getInterfaceClass());
    }

    public static Object unmarshal(Object object) {
        if(object instanceof SerializableAspectInstance) {
            object = ((SerializableAspectInstance) object).getObject();
        }
        return object;
    }
}
