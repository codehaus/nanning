package com.tirsen.nanning.samples.prevayler;

import java.io.Serializable;
import java.security.AccessController;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;

import com.tirsen.nanning.Invocation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

public class IdentifyingCall extends MarshallingCall {
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

        setInvocation(invocation);

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

    protected Marshaller createMarshaller() {
        return new IdentifyingMarshaller();
    }

    public Subject getSubject() {
        if (principals == null && publicCredentials == null && privateCredentials == null) {
            return null;
        } else {
            return new Subject(false, principals == null ? new HashSet() : principals, publicCredentials == null ? new HashSet() : publicCredentials, privateCredentials == null ? new HashSet() : privateCredentials);
        }
    }

}
