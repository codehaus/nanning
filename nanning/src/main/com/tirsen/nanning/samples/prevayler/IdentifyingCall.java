package com.tirsen.nanning.samples.prevayler;

import java.io.InputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;

import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.Interceptor;
import com.tirsen.nanning.Invocation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.io.IOUtil;

public class IdentifyingCall extends Call {
    static final long serialVersionUID = -6836192619875407405L;
    private Set principals;
    private Set privateCredentials;
    private Set publicCredentials;

    private static final Predicate isSerializable = new Predicate() {
        public boolean evaluate(Object o) {
            return o instanceof Serializable;
        }
    };

    public IdentifyingCall(Invocation invocation) throws Exception {
        super(invocation);
        Subject subject = Subject.getSubject(AccessController.getContext());
        if (subject != null) {
            principals = new HashSet(subject.getPrincipals());
            CollectionUtils.filter(principals, isSerializable);
            privateCredentials = new HashSet(subject.getPrivateCredentials());
            CollectionUtils.filter(privateCredentials, isSerializable);
            publicCredentials = new HashSet(subject.getPublicCredentials());
            CollectionUtils.filter(publicCredentials, isSerializable);
        }
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
        if (Identity.isPrimitive(o)) {
            return o;
        } else if (o instanceof InputStream) {
            return new Identity(InputStream.class, IOUtil.toByteArray(((InputStream) o)));
        } else if (Identity.isService(o.getClass())) {
            return new Identity(o.getClass(), getClassIdentifier());
        } else if (Identity.isEntity(o.getClass())) {
            if (CurrentPrevayler.getSystem().hasObjectID(o)) {
                return new Identity(o.getClass(), new Long(CurrentPrevayler.getSystem().getObjectID(o)));
            } else {
                // object is not part of target prevalent-system, marshal by value and assign ID at execution
                return o;
            }
        } else {
            return o;
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
        if (Identity.isPrimitive(o)) {
            return o;
        } else if (o instanceof Identity) {
            return resolve((Identity) o);
        } else if (Identity.isEntity(o.getClass())) {
            if (!CurrentPrevayler.getSystem().hasObjectID(o)) {
                registerObjectIDsRecursive(o);
            }
            return o;
        } else {
            return o;
        }
    }

    private void registerObjectIDsRecursive(Object o) {
        final IdentifyingSystem system = CurrentPrevayler.getSystem();
        ObjectGraphVisitor.visit(o, new ObjectGraphVisitor() {
            protected void visit(Object o) {
                if (o instanceof String) {
                    return;
                }
                if (Identity.isEntity(o.getClass())) {
                    assert !system.hasObjectID(o) :
                            "you're mixing object in prevayler with objects outside, this will lead to unpredictable results, " +
                            "so I've banished that sort of behaviour with this assert here, the object that was inside prevayler was " + o;
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

    public Subject getSubject() {
        if (principals == null && publicCredentials == null && privateCredentials == null) {
            return null;
        } else {
            return new Subject(false, principals == null ? new HashSet() : principals, publicCredentials == null ? new HashSet() : publicCredentials, privateCredentials == null ? new HashSet() : privateCredentials);
        }
    }

}
