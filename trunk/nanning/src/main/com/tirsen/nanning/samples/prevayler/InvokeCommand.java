package com.tirsen.nanning.samples.prevayler;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;

import javax.security.auth.Subject;

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
            Subject subject = call.getSubject();
            if (logger.isDebugEnabled()) {

                List argsList = Collections.EMPTY_LIST;
                if (args != null) {
                    argsList = Arrays.asList(args);
                }
                logger.debug("invoking method " + method + " on " + target);
                logger.debug("args " + argsList);
                logger.debug("user " + subject);
            }
            Serializable serializable = execute(system, subject, method, target, args);
            logger.debug("success!");
            return serializable;
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
            PrevalentSystem system, Subject subject,
            final Method method, final Object unmarshalledTarget, final Object[] unmarshalledArgs)
            throws Exception
    {
        if (subject != null) {
            try {
                return (Serializable) Subject.doAs(subject, new PrivilegedExceptionAction() {
                    public Object run() throws Exception {
                        return (Serializable) method.invoke(unmarshalledTarget, unmarshalledArgs);
                    }
                });
            } catch (java.security.PrivilegedActionException e) {
                throw e.getException();
            }
        } else {
            return (Serializable) method.invoke(unmarshalledTarget, unmarshalledArgs);
        }
    }

}
