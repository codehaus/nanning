package com.tirsen.nanning.samples.prevayler;

import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;

import com.tirsen.nanning.Invocation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

public class MarshallingCall extends Call {
    static final long serialVersionUID = -8607314000649422353L;
    
    private transient Marshaller marshaller;

    private Set principals;
    private Set privateCredentials;
    private Set publicCredentials;

    private static final Predicate isSerializable = new Predicate() {
        public boolean evaluate(Object o) {
            return o instanceof Serializable;
        }
    };


    public MarshallingCall() {
        Subject subject = Subject.getSubject(AccessController.getContext());
        if (subject != null) {
            principals = new HashSet(subject.getPrincipals());
            CollectionUtils.filter(principals, isSerializable);
            privateCredentials = new HashSet(subject.getPrivateCredentials());
            CollectionUtils.filter(privateCredentials, isSerializable);
            publicCredentials = new HashSet(subject.getPublicCredentials());
            CollectionUtils.filter(publicCredentials, isSerializable);
        }
    }

    public MarshallingCall(Invocation invocation, Marshaller marshaller) throws Exception {
        this();
        setMarshaller(marshaller);
        setInvocation(invocation);
    }

    public Subject getSubject() {
        if (principals == null && publicCredentials == null && privateCredentials == null) {
            return null;
        } else {
            return new Subject(false, principals == null ? new HashSet() : principals, publicCredentials == null ? new HashSet() : publicCredentials, privateCredentials == null ? new HashSet() : privateCredentials);
        }
    }

    public void setMarshaller(Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    public Marshaller getMarshaller() {
        if (marshaller == null) {
            marshaller = createMarshaller();
        }
        return marshaller;
    }

    protected Marshaller createMarshaller() {
        return null;
    }

    protected void setInvocation(Invocation invocation) throws Exception {
        super.setInvocation(invocation);
        target = marshal(target);
        args = marshalArguments(invocation.getArgs());
    }

    protected Object[] marshalArguments(Object[] args) throws Exception {
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
        return getMarshaller().marshal(o);
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

    protected Object unmarshal(Object o) {
        return getMarshaller().unmarshal(o);
    }

    public Object[] getArgs() {
        return unmarshalArguments(args);
    }

    public Object getTarget() {
        assert target != null;
        return unmarshal(target);
    }

    public void setTarget(Object target) {
        this.target = marshaller.marshal(target);
    }

    public Object invoke() throws Exception {
        Subject subject = getSubject();

        if (subject != null) {
            try {
                return (Serializable) Subject.doAs(subject, new PrivilegedExceptionAction() {
                    public Object run() throws Exception {
                        return MarshallingCall.super.invoke();
                    }
                });
            } catch (java.security.PrivilegedActionException e) {
                throw e.getException();
            }
        } else {
            return super.invoke();
        }
    }
}
