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

    public IdentifyingCall(Invocation invocation) throws Exception {
        setInvocation(invocation);
    }

    protected Marshaller createMarshaller() {
        return new IdentifyingMarshaller();
    }
}
