package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.Interceptor;
import com.tirsen.nanning.attribute.Attributes;

public class IdentifyingCall extends Call {
    public IdentifyingCall(Invocation invocation) {
        super(invocation);
        target = marshal(target);
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
            if (isService(getInterfaceClass())) {
                return new Identity(getInterfaceClass(), getClassIdentifier());
            }
            if (isEntity(getInterfaceClass())) {
                if (CurrentPrevayler.getSystem().hasObjectID(o)) {
                    return new Identity(o.getClass(), new Long(CurrentPrevayler.getSystem().getObjectID(o)));
                } else {
                    // object is not part of target prevalent-system, marshal by value and assign ID at execution
                    return o;
                }
            }

        }

        throw new IllegalArgumentException("Can't marshal " + o);
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
        if (o == null) {
            return null;
        } else if (o instanceof Number) {
            return o;
        } else if (o instanceof String) {
            return o;
        } else if (o instanceof Character) {
            return o;
        } else if (o instanceof Identity) {
            return resolve((Identity) o);
        } else if (isEntity(o.getClass())) {
            registerObjectIDsRecursive(o);
            return o;
        } else {
            throw new IllegalArgumentException("Can't resolve " + o);
        }
    }

    private void registerObjectIDsRecursive(Object o) {
        final IdentifyingSystem system = CurrentPrevayler.getSystem();
        ObjectGraphVisitor.visit(o, new ObjectGraphVisitor() {
            protected void visit(Object o) {
                if (isEntity(o.getClass())) {
                    assert !system.hasObjectID(o) :
                            "you're mixing object in prevayler with objects outside, this will lead to unpredictable results, " +
                            "so I've banished that sort of behaviour with this assert here";
                    system.registerObjectID(o);
                }
                // for performance, skip the proxy part of all aspected objects
                if (Aspects.isAspectObject(o)) {
                    Object[] targets = Aspects.getTargets(o);
                    for (int i = 0; i < targets.length; i++) {
                        super.visit(targets[i]);
                    }
                    Interceptor[] interceptors = Aspects.getInterceptors(o);
                    for (int i = 0; i < interceptors.length; i++) {
                        super.visit(interceptors[i]);
                    }
                } else {
                    super.visit(o);
                }
            }
        });
    }

    protected Object resolve(Identity identity) {
        Class objectClass = identity.getObjectClass();
        if (isService(objectClass)) {
            return Aspects.getCurrentAspectFactory().newInstance(identity.getIdentifier());
        }
        if (isEntity(objectClass)) {
            return CurrentPrevayler.getSystem().getObjectWithID(((Long) identity.getIdentifier()).longValue());
        }
        throw new IllegalArgumentException("Can't resolve objects of " + objectClass);
    }

    private boolean isEntity(Class objectClass) {
        return Attributes.hasInheritedAttribute(objectClass, "entity");
    }

    private boolean isService(Class objectClass) {
        return Attributes.hasInheritedAttribute(objectClass, "service");
    }

    public Object[] getArgs() {
        return unmarshalArguments(args);
    }

    public Object getTarget() {
        assert target != null;
        return unmarshal(target);
    }
}
