package com.tirsen.nanning.samples.prevayler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.prevayler.PrevalentSystem;
import org.prevayler.Command;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.tirsen.nanning.Invocation;

public class InvokeCommand implements Command {
    private static final Log logger = LogFactory.getLog(InvokeCommand.class);
    private IdentifyingCall call;

    public InvokeCommand(Invocation invocation) {
        call = new IdentifyingCall(invocation);
    }

    public Serializable execute(PrevalentSystem system) throws Exception {
        Object target = call.getTarget();
        Object[] args = call.getArgs();
        Method method = call.getMethod();
        return execute(system, method, target, args);
    }

    protected Serializable execute(
            PrevalentSystem system, Method method, Object unmarshalledTarget, Object[] unmarshalledArgs) {
        PrevaylerInterceptor.getPrevaylerInterceptor().enterTransaction();
        CurrentPrevayler.setSystem((IdentifyingSystem) system);
        try {
            try {
                return (Serializable) method.invoke(unmarshalledTarget, unmarshalledArgs);
            } catch (Exception e) {
                logger.fatal("Failed to execute command.", e);
                throw new RuntimeException(e);
            }
        } finally {
            PrevaylerInterceptor.getPrevaylerInterceptor().exitTransaction();
        }
    }

}
