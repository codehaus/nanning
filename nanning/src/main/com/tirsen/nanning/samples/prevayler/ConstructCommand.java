package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.ConstructionInvocation;
import com.tirsen.nanning.definition.AspectClass;
import com.tirsen.nanning.definition.AspectRepository;
import com.tirsen.nanning.Aspects;

import java.io.Serializable;

import org.prevayler.PrevalentSystem;
import org.prevayler.Command;

public class ConstructCommand implements Command {
    private Object classIdentifier;

    public ConstructCommand(ConstructionInvocation invocation) {
        classIdentifier = Aspects.getAspectInstance(invocation.getProxy()).getClassIdentifier();
    }

    public Serializable execute(PrevalentSystem prevalentSystem) throws Exception {
        PrevaylerInterceptor.getPrevaylerInterceptor().enterTransaction();
        CurrentPrevayler.setSystem((IdentifyingSystem) prevalentSystem);
        try {
            Object o = Aspects.getCurrentAspectFactory().newInstance(classIdentifier);
            CurrentPrevayler.getSystem().getObjectID(o);
            return (Serializable) o;
        } finally {
            PrevaylerInterceptor.getPrevaylerInterceptor().exitTransaction();
        }
    }
}
