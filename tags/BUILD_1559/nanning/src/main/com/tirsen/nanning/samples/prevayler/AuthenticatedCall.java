package com.tirsen.nanning.samples.prevayler;

import java.util.HashSet;
import java.util.Set;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.security.PrivilegedActionException;
import java.io.Serializable;

import javax.security.auth.Subject;

import com.tirsen.nanning.Invocation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

public class AuthenticatedCall extends Call {
    static final long serialVersionUID = 8405907347881334801L;

    protected Set principals;
    protected Set privateCredentials;
    protected Set publicCredentials;

    private static final Predicate isSerializable = new Predicate() {
        public boolean evaluate(Object o) {
            return o instanceof Serializable;
        }
    };

    public AuthenticatedCall() {
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

    public AuthenticatedCall(Invocation invocation) throws Exception {
        this();
        setInvocation(invocation);
    }

    public Subject getSubject() {
        if (principals == null && publicCredentials == null && privateCredentials == null) {
            return null;
        } else {
            return new Subject(false, principals == null ? new HashSet() : principals, publicCredentials == null ? new HashSet() : publicCredentials, privateCredentials == null ? new HashSet() : privateCredentials);
        }
    }

    public Object invoke() throws Exception {
        try {
            return Subject.doAs(getSubject(), new PrivilegedExceptionAction() {
                public Object run() throws Exception {
                    return AuthenticatedCall.super.invoke();
                }
            });
        } catch (PrivilegedActionException e) {
            throw e.getException();
        }
    }
}
