package com.tirsen.nanning.samples.prevayler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.security.auth.Subject;

import com.tirsen.nanning.Invocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.prevayler.util.TransactionWithQuery;

public class InvokeCommand extends TransactionWithQuery {
    static final long serialVersionUID = 320681517664792343L;

    private static final Log logger = LogFactory.getLog(InvokeCommand.class);
    private AuthenticatedCall call;

    public InvokeCommand(Invocation invocation) throws Exception {
        call = new AuthenticatedCall(invocation);
    }

    protected Object executeAndQuery(Object system) throws Exception {
        Object prev = null;
        if (CurrentPrevayler.isInitialized()) {
            prev = CurrentPrevayler.getSystem();
        }
        if (!CurrentPrevayler.hasSystem() || CurrentPrevayler.getSystem() != system) {
            CurrentPrevayler.setSystem(system);
        }
        CurrentPrevayler.enterTransaction();
        try {
            if (logger.isDebugEnabled()) {
                Object target = call.getTarget();
                Object[] args = call.getArgs();
                Method method = call.getMethod();
                Subject subject = call.getSubject();

                List argsList = Collections.EMPTY_LIST;
                if (args != null) {
                    argsList = Arrays.asList(args);
                }
                logger.debug("invoking method " + method + " on " + target);
                logger.debug("args " + argsList);
                logger.debug("user " + subject);
            }

            Object result = call.invoke();
            logger.debug("success!");
            return result;
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

    public Call getCall() {
        return call;
    }
}
