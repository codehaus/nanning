package com.tirsen.nanning.samples.rmi;

import java.security.AccessController;
import java.util.HashMap;
import java.util.Map;
import java.io.*;

import javax.security.auth.Subject;

import com.tirsen.nanning.AspectFactory;
import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.samples.prevayler.MarshallingCall;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RemoteCallServer {
    private static final Log logger = LogFactory.getLog(SocketRemoteCallServer.class);
    private AspectFactory aspectRepository;
    private Map naming = new HashMap();
    private RemoteMarshaller marshaller;

    public RemoteCallServer(ServerConnectionManager connectionManager) {
        marshaller = new RemoteMarshaller(connectionManager);
    }

    public void processCall(InputStream commandStream, OutputStream resultStream) {
        Aspects.setContextAspectFactory(aspectRepository);
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
            output.writeObject(result);
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

    public void setAspectFactory(AspectFactory aspectRepository) {
        this.aspectRepository = aspectRepository;
    }

    public void bind(String name, Object o) {
        naming.put(name, o);
    }

    public void reset() {
        marshaller.reset();
    }
}
