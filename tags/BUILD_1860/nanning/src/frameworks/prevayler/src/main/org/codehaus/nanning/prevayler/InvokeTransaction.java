package org.codehaus.nanning.prevayler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.nanning.Invocation;
import org.codehaus.nanning.AssertionException;
import org.codehaus.nanning.util.WrappedException;
import org.prevayler.TransactionWithQuery;

import javax.security.auth.Subject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class InvokeTransaction implements TransactionWithQuery {
    private static final Log logger = LogFactory.getLog(InvokeTransaction.class);
    static final long serialVersionUID = 320681517664792343L;
    private byte[] marshalledCall;
    private String methodName;

    public InvokeTransaction(Invocation invocation) throws Exception {
        methodName = invocation.getMethod().getName();
        marshalledCall = marshalCall(new AuthenticatedCall(invocation));
    }

    private byte[] marshalCall(AuthenticatedCall call) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            MarshallingOutputStream outputStream = new MarshallingOutputStream(bytes, new IdentifyingMarshaller());
            outputStream.writeObject(call);
            outputStream.flush();
            return bytes.toByteArray();
        } catch (IOException e) {
            throw new WrappedException("Could not marshal call", e);
        }
    }

    private AuthenticatedCall unmarshalCall() {
        try {
            MarshallingInputStream inputStream =
                    new MarshallingInputStream(new ByteArrayInputStream(marshalledCall), new IdentifyingMarshaller());
            return (AuthenticatedCall) inputStream.readObject();
        } catch (IOException e) {
            throw new WrappedException("Could not marshal call", e);
        } catch (ClassNotFoundException e) {
            throw new WrappedException("Could not marshal call", e);
        }
    }

    public Object executeAndQuery(Object system, Date executionTime) throws Exception {

        CurrentPrevayler.enterTransaction(system);

        registerObjectIDForSystem(system);
        try {
            CurrentPrevayler.setClock(executionTime);

            AuthenticatedCall call = unmarshalCall();
            logInvocation(call);
            Object result = call.invoke();
            logger.debug("success!");
            return result;

        } catch (Exception e) {
            handleException(e);
            throw e;
        } finally {
            CurrentPrevayler.clearClock();
            CurrentPrevayler.exitTransaction();
        }
    }

    private void registerObjectIDForSystem(Object system) {
        IdentifyingSystem identifyingSystem = (IdentifyingSystem) system;
        if (!((Identifiable) identifyingSystem).hasObjectID()) {
            identifyingSystem.register(identifyingSystem);
            if (((Identifiable) identifyingSystem).getObjectID() != 0) {
                throw new AssertionException();
            }
        }
    }

    private void handleException(Exception e) throws Exception {
        /* Unwrap the InvocationTargetException */
        if (e instanceof InvocationTargetException) {
            InvocationTargetException invocationTargetException = (InvocationTargetException) e;
            if (invocationTargetException.getTargetException() instanceof Exception) {
                e = (Exception) invocationTargetException.getTargetException();
            }
        }
        logger.error("Failed to execute command.", e);

        throw e;
    }

    private void logInvocation(AuthenticatedCall call) {
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
        return unmarshalCall();
    }

    public String getMethodName() {
        return methodName;
    }

}
