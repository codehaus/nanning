package com.tirsen.nanning.samples.rmi;

import java.io.*;
import java.security.AccessController;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;

import com.tirsen.nanning.AspectFactory;
import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.samples.prevayler.MarshallingCall;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RemoteCallServer {
    private static final Log logger = LogFactory.getLog(SocketRemoteCallServer.class);
    
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
            ObjectInputStream input = new ObjectInputStream(commandStream);
            Object command = input.readObject();

            Object result = processCall(command);

            ObjectOutputStream output = new ObjectOutputStream(resultStream);
            try {
                output.writeObject(result);
            } catch (NotSerializableException e) {
                String message = "Could not serialize object with class " + result.getClass();
                assert false : message;
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
        if (command instanceof MarshallingCall) {
            MarshallingCall call = (MarshallingCall) command;

            result = processRemoteCall(call);

        } else if (command instanceof NamingLookup) {
            NamingLookup namingLookup = (NamingLookup) command;

            result = processNamingLookup(namingLookup);
        } else {
            result = new ExceptionThrown(new RuntimeException("No such command exception."));
        }

        result = marshaller.marshal(result);

        return result;
    }

    private Object processNamingLookup(NamingLookup namingLookup) {
        Object result;
        String name = namingLookup.getName();
        result = naming.get(name);
        return result;
    }

    private Object processRemoteCall(MarshallingCall call) {
        Object result;
        try {
            call.setMarshaller(marshaller);
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
