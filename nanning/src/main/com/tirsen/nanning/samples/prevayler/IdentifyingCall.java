package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.Invocation;

public class IdentifyingCall extends MarshallingCall {
    static final long serialVersionUID = -6836192619875407405L;

    public IdentifyingCall(Invocation invocation) throws Exception {
        setInvocation(invocation);
    }

    protected Marshaller createMarshaller() {
        return new IdentifyingMarshaller();
    }
}
