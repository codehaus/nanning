package com.tirsen.nanning.samples.rmi;

import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.samples.prevayler.Call;

public class RemoteServiceCall extends Call {
    public RemoteServiceCall(Invocation invocation) {
        super(invocation);
        target = null;
    }

    public Object getTarget() {
        return Aspects.getCurrentAspectFactory().newInstance(getClassIdentifier());
    }
}
