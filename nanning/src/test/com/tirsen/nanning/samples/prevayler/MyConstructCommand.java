package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.samples.prevayler.ConstructCommand;
import com.tirsen.nanning.ConstructionInvocation;
import com.tirsen.nanning.AspectClass;
import com.tirsen.nanning.Aspects;

import java.io.Serializable;

import org.prevayler.PrevalentSystem;

public class MyConstructCommand implements ConstructCommand {
    private Class interfaceClass;

    public void setInvocation(ConstructionInvocation invocation) {
        AspectClass aspectClass = Aspects.getAspectClass(invocation.getProxy());
        interfaceClass = aspectClass.getInterfaceClass();
    }

    public Serializable execute(PrevalentSystem prevalentSystem) throws Exception {
        PrevaylerTest.getNoPrevaylerAspectRepository().newInstance(interfaceClass);
        return null;
    }
}
