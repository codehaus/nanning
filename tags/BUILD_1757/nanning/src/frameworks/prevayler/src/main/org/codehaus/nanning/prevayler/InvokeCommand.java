package org.codehaus.nanning.prevayler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.nanning.Invocation;
import org.codehaus.nanning.AssertionException;
import org.prevayler.TransactionWithQuery;

import javax.security.auth.Subject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class InvokeCommand implements TransactionWithQuery {
    private static final Log logger = LogFactory.getLog(InvokeCommand.class);
    static final long serialVersionUID = 320681517664792343L;

    private AuthenticatedCall call;

    public InvokeCommand(Invocation invocation) throws Exception {
        call = new IdentifyingCall(invocation);
    }

    public Object executeAndQuery(Object system, Date executionTime) throws Exception {

        CurrentPrevayler.enterTransaction(system);

        IdentifyingSystem identifyingSystem = (IdentifyingSystem) system;
        if (!identifyingSystem.hasObjectID(identifyingSystem)) {
            identifyingSystem.registerObjectID(identifyingSystem);
            if (identifyingSystem.getObjectID(identifyingSystem) != 0) {
                throw new AssertionException();
            }
        }

        try {
            logInvocation();
            Object result = call.invoke();
            logger.debug("success!");
            return result;
        } catch (Exception e) {

            /** Unwrap the invocation target exceptions */
            if (e instanceof InvocationTargetException) {
                InvocationTargetException invocationTargetException = (InvocationTargetException) e;
// TODO: fix this
//                if (invocationTargetException.getCause() instanceof Exception) {
//                    e = (Exception) e.getCause();
//                }
            }
            logger.error("Failed to execute command.", e);

            throw e;
        } finally {
            CurrentPrevayler.exitTransaction();
        }
    }

    private void logInvocation() {
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
    }

    public Call getCall() {
        return call;
    }

}