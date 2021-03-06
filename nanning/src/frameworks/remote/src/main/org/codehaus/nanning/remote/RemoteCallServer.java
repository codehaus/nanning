package org.codehaus.nanning.remote;

import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.OutputStream;
import java.security.AccessController;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.nanning.AspectFactory;
import org.codehaus.nanning.Aspects;
import org.codehaus.nanning.prevayler.Call;
import org.codehaus.nanning.prevayler.MarshallingInputStream;
import org.codehaus.nanning.prevayler.MarshallingOutputStream;

public class RemoteCallServer {
    private static final Log logger = LogFactory.getLog(RemoteCallServer.class);

    private AspectFactory aspectFactory;
    private Map naming = new HashMap();
    private RemoteMarshaller marshaller;

    public RemoteCallServer(ServerConnectionManager connectionManager) {
        marshaller = RemoteMarshaller.createServerSideMarshaller(connectionManager);
    }

    public void setAspectFactory(AspectFactory aspectRepository) {
        this.aspectFactory = aspectRepository;
    }

    public void processCall(InputStream commandStream, OutputStream resultStream) {
        Aspects.setContextAspectFactory(aspectFactory);
        Subject subject = Subject.getSubject(AccessController.getContext());
        if (subject != null) {
            subject.getPrincipals().clear();
            subject.getPrivateCredentials().clear();
            subject.getPublicCredentials().clear();
        }

        try {
            MarshallingInputStream input = new MarshallingInputStream(commandStream, marshaller);
            Object command = input.readObject();

            Object result = processCall(command);

            MarshallingOutputStream output = new MarshallingOutputStream(resultStream, marshaller);
            try {
                output.writeObject(result);
            } catch (NotSerializableException e) {
                String message = "Could not serialize object with class " + result.getClass();
                logger.fatal(message);
            }
            output.flush();

        } catch (IOException e) {
            logger.error("error communicating with client", e);
        } catch (ClassNotFoundException e) {
            logger.error("error communicating with client", e);
        }
    }


    private Object processCall(Object command) {
        Object result;
        if (command instanceof Call) {
            Call call = (Call) command;

            result = processRemoteCall(call);

        } else if (command instanceof NamingLookup) {
            NamingLookup namingLookup = (NamingLookup) command;

            result = processNamingLookup(namingLookup);
        } else {
            result = new ExceptionThrown(new RuntimeException("No such command exception."));
        }

        return result;
    }

    private Object processNamingLookup(NamingLookup namingLookup) {
        Object result;
        String name = namingLookup.getName();
        result = naming.get(name);
        return result;
    }

    private Object processRemoteCall(Call call) {
        Object result;
        try {
            result = call.invoke();
        } catch (Throwable e) {
            logger.error("error executing call", e);
            result = new ExceptionThrown(e);
        }
        return result;
    }

    public void bind(String name, Object o) {
        naming.put(name, o);
    }

    public void reset() {
        marshaller.reset();
    }
}
