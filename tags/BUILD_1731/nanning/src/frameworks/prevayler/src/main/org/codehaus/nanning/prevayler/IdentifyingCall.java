package org.codehaus.nanning.prevayler;

import org.codehaus.nanning.Invocation;

public class IdentifyingCall extends MarshallingCall {
    static final long serialVersionUID = 8545095375648929875L;

    public IdentifyingCall(Invocation invocation) throws Exception {
        super(invocation, new IdentifyingMarshaller());
    }

    protected Marshaller createMarshaller() {
        return new IdentifyingMarshaller();
    }
}
