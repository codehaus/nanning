package com.tirsen.nanning.prevayler;

import com.tirsen.nanning.ConstructionInterceptor;
import com.tirsen.nanning.ConstructionInvocation;
import com.tirsen.nanning.attribute.Attributes;
import com.tirsen.nanning.prevayler.CurrentPrevayler;
import com.tirsen.nanning.prevayler.IdentifyingSystem;


public class RegisterObjectInterceptor implements ConstructionInterceptor {

    public Object construct(ConstructionInvocation invocation) {
        Object object = invocation.getProxy();

        if (CurrentPrevayler.hasSystem() && CurrentPrevayler.isInTransaction()) {
            IdentifyingSystem system = (IdentifyingSystem) CurrentPrevayler.getSystem();
            if (!system.hasObjectID(object)) {
                system.registerObjectID(object);
            }
        }
        return object;
    }
}
