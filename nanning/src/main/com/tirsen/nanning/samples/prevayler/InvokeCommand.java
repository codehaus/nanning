package com.tirsen.nanning.samples.prevayler;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.tirsen.nanning.Invocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.prevayler.Command;
import org.prevayler.PrevalentSystem;

public class InvokeCommand implements Command {
    static final long serialVersionUID = 320681517664792343L;

    private static final Log logger = LogFactory.getLog(InvokeCommand.class);
    private IdentifyingCall call;

    public InvokeCommand(Invocation invocation) throws Exception {
        call = new IdentifyingCall(invocation);
    }

    public Serializable execute(PrevalentSystem system) throws Exception {
        IdentifyingSystem prev = null;
        if (CurrentPrevayler.isInitialized()) {
            prev = CurrentPrevayler.getSystem();
        }
        CurrentPrevayler.setSystem((IdentifyingSystem) system);
        CurrentPrevayler.enterTransaction();
        try {
            Object target = call.getTarget();
            Object[] args = call.getArgs();
            Method method = call.getMethod();
            return execute(system, method, target, args);
        } catch (Exception e) {

            /** Unwrap the invocation target exceptions */
            if (e instanceof InvocationTargetException) {
                InvocationTargetException invocationTargetException = (InvocationTargetException) e;
                if (invocationTargetException.getCause() instanceof Exception) {
                    e = (Exception) e.getCause();
                }
            }
            logger.error("Failed to execute command.", e);

            throw e;
        } finally {
            CurrentPrevayler.exitTransaction();
            CurrentPrevayler.setSystem(prev);
        }
    }

    protected Serializable execute(
            PrevalentSystem system, Method method, Object unmarshalledTarget, Object[] unmarshalledArgs)
            throws Exception
    {
        return (Serializable) method.invoke(unmarshalledTarget, unmarshalledArgs);
    }

}
