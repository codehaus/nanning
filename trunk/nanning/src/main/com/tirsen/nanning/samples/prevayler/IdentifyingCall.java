package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.attribute.Attributes;

public class IdentifyingCall extends Call {
    public IdentifyingCall(Invocation invocation) {
        super(invocation);
        target = identify(target);
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
            return resolve((Identity) o);
        } else {
            throw new IllegalArgumentException("Can't resolve " + o);
        }
    }

    protected Identity identify(Object object) {
        if (Attributes.hasInheritedAttribute(getInterfaceClass(), "service")) {
            return null;
        }
        if (Attributes.hasInheritedAttribute(getInterfaceClass(), "entity")) {
            return new Identity(object.getClass(), new Long(CurrentPrevayler.getSystem().getObjectID(object)));
        }

        throw new IllegalArgumentException("Can't identify " + object);
    }

    protected Object resolve(Identity identity) {
        if (Attributes.hasInheritedAttribute(identity.getObjectClass(), "entity")) {
            return CurrentPrevayler.getSystem().getObjectWithID(((Long) identity.getIdentifier()).longValue());
        }
        throw new IllegalArgumentException("Can't resolve objects of " + identity.getObjectClass());
    }

    public Object[] getArgs() {
        return unmarshalArguments(args);
    }

    public Object getTarget() {
        if (target == null) {
            return Aspects.getCurrentAspectFactory().newInstance(getClassIdentifier());
        } else {
            return resolve((Identity) target);
        }
    }
}
