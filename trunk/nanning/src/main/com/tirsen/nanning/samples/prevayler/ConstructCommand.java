package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.ConstructionInvocation;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.prevayler.Command;
import org.prevayler.PrevalentSystem;

import java.io.Serializable;

public class ConstructCommand implements Command {
    private static final Log logger = LogFactory.getLog(ConstructCommand.class);
    static final long serialVersionUID = 8814750796281720268L;
    
    private Object classIdentifier;

    public ConstructCommand(ConstructionInvocation invocation) {
        classIdentifier = Aspects.getAspectInstance(invocation.getProxy()).getClassIdentifier();
    }

    public Serializable execute(PrevalentSystem prevalentSystem) throws Exception {
        logger.debug("executing " + this + " on system " + prevalentSystem);
        CurrentPrevayler.setSystem((IdentifyingSystem) prevalentSystem);
        CurrentPrevayler.enterTransaction();
        try {
            Object o = Aspects.getCurrentAspectFactory().newInstance(classIdentifier);
            assert CurrentPrevayler.getSystem().hasObjectID(o);
            return (Serializable) o;
        } finally {
            CurrentPrevayler.exitTransaction();
        }
    }

    public String toString() {
        return new ToStringBuilder(this).append("class", classIdentifier).toString();
    }
}
