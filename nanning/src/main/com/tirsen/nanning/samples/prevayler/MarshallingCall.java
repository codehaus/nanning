package com.tirsen.nanning.samples.prevayler;

import java.io.Serializable;
import java.security.PrivilegedExceptionAction;

import javax.security.auth.Subject;

import com.tirsen.nanning.Invocation;

public class MarshallingCall extends AuthenticatedCall {
    static final long serialVersionUID = -8607314000649422353L;

    private transient Marshaller marshaller;

    public MarshallingCall(Invocation invocation, Marshaller marshaller) throws Exception {
        setMarshaller(marshaller);
        setInvocation(invocation);
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

    protected Object marshal(Object o) {
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

        Object result;

        if (subject != null) {
            try {
                result = Subject.doAs(subject, new PrivilegedExceptionAction() {
                                            public Object run() throws Exception {
                                                return MarshallingCall.super.invoke();
                                            }
                                        });
            } catch (java.security.PrivilegedActionException e) {
                throw e.getException();
            }
        } else {
            result = super.invoke();
        }

        return result;
    }
}
