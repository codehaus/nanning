package org.codehaus.nanning.prevayler;

import org.codehaus.nanning.ConstructionInterceptor;
import org.codehaus.nanning.ConstructionInvocation;
import org.codehaus.nanning.prevayler.CurrentPrevayler;
import org.codehaus.nanning.prevayler.IdentifyingSystem;


public class RegisterObjectInterceptor implements ConstructionInterceptor {
    public Object construct(ConstructionInvocation invocation) {
        Object object = invocation.getProxy();

        if (CurrentPrevayler.isInTransaction()) {
            IdentifyingSystem system = (IdentifyingSystem) CurrentPrevayler.getSystem();
            system.register(object);
        }
        return object;
    }
}
