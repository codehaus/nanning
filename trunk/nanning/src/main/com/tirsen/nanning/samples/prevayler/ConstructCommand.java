package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.ConstructionInvocation;
import com.tirsen.nanning.AspectClass;
import com.tirsen.nanning.Aspects;

import java.io.Serializable;

import org.prevayler.PrevalentSystem;
import org.prevayler.Command;

public class ConstructCommand implements Command {
    private Class interfaceClass;

    public ConstructCommand(ConstructionInvocation invocation) {
        AspectClass aspectClass = Aspects.getAspectClass(invocation.getProxy());
        interfaceClass = aspectClass.getInterfaceClass();
    }

    public Serializable execute(PrevalentSystem prevalentSystem) throws Exception {
        PrevaylerInterceptor.getPrevaylerInterceptor().enterTransaction();
        CurrentPrevayler.setSystem((IdentifyingSystem) prevalentSystem);
        try {
            Object o = Aspects.getCurrentAspectRepository().newInstance(interfaceClass);
            CurrentPrevayler.getSystem().registerOID(o);
            return (Serializable) o;
        } finally {
            PrevaylerInterceptor.getPrevaylerInterceptor().exitTransaction();
        }
    }
}
