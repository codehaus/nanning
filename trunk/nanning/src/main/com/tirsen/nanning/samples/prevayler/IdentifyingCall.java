package com.tirsen.nanning.samples.prevayler;

import java.io.InputStream;

import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.Interceptor;
import com.tirsen.nanning.Invocation;
import org.apache.commons.io.IOUtil;

public class IdentifyingCall extends Call {
    public IdentifyingCall(Invocation invocation) throws Exception {
        super(invocation);
        target = marshal(target);
        args = marshalArguments(invocation.getArgs());
    }

    private Object[] marshalArguments(Object[] args) throws Exception {
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

    protected Object marshal(Object o) throws Exception {
        if (Identity.isMarshalByValue(o)) {
            return o;
        } else if (o instanceof InputStream) {
            return new Identity(InputStream.class, IOUtil.toByteArray(((InputStream) o)));
        } else {
            if (Identity.isService(o.getClass())) {
                return new Identity(o.getClass(), getClassIdentifier());
            }
            if (Identity.isEntity(o.getClass())) {
                if (CurrentPrevayler.getSystem().hasObjectID(o)) {
                    return new Identity(o.getClass(), new Long(CurrentPrevayler.getSystem().getObjectID(o)));
                } else {
                    // object is not part of target prevalent-system, marshal by value and assign ID at execution
                    return o;
                }
            }

        }

        throw new IllegalArgumentException("Can't marshal " + o +
                                           " could it be an entity or service without the proper attribute?"+
                                           " (Use 'entity', 'service' or 'marshal-by-value'.)");
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
        if (Identity.isMarshalByValue(o)) {
            return o;
        } else if (o instanceof Identity) {
            return resolve((Identity) o);
        } else if (Identity.isEntity(o.getClass())) {
            if (!CurrentPrevayler.getSystem().hasObjectID(o)) {
                registerObjectIDsRecursive(o);
            }
            return o;
        } else {
            throw new IllegalArgumentException("Can't resolve " + o);
        }
    }

    private void registerObjectIDsRecursive(Object o) {
        final IdentifyingSystem system = CurrentPrevayler.getSystem();
        ObjectGraphVisitor.visit(o, new ObjectGraphVisitor() {
            protected void visit(Object o) {
                if (Identity.isMarshalByValue(o)) {
                    return;
                }
                if (Identity.isEntity(o.getClass())) {
                    assert !system.hasObjectID(o) :
                            "you're mixing object in prevayler with objects outside, this will lead to unpredictable results, " +
                            "so I've banished that sort of behaviour with this assert here";
                    system.registerObjectID(o);
                    super.visit(o);
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
        return identity.resolve();
    }

    public Object[] getArgs() {
        return unmarshalArguments(args);
    }

    public Object getTarget() {
        assert target != null;
        return unmarshal(target);
    }

}
