package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.ConstructionInterceptor;
import com.tirsen.nanning.ConstructionInvocation;
import com.tirsen.nanning.attribute.Attributes;
import com.tirsen.nanning.samples.prevayler.CurrentPrevayler;


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

    public boolean interceptsConstructor(Class klass) {
        return Attributes.hasInheritedAttribute(klass, "entity");
    }
}
