package org.codehaus.nanning.remote;

import java.io.IOException;

import org.codehaus.nanning.*;
import org.codehaus.nanning.remote.CouldNotConnectException;
import org.codehaus.nanning.remote.ExceptionThrown;
import org.codehaus.nanning.prevayler.MarshallingInputStream;
import org.codehaus.nanning.prevayler.MarshallingOutputStream;
import org.codehaus.nanning.config.Aspect;
import org.codehaus.nanning.prevayler.Call;
import org.codehaus.nanning.prevayler.Marshaller;
import org.codehaus.nanning.prevayler.AuthenticatedCall;

public class RemoteAspect implements Aspect, MethodInterceptor {
    private Marshaller marshaller;

    public Object invoke(Invocation invocation) throws Throwable {
        if (!(invocation.getTarget() instanceof RemoteIdentity)) {
            throw new AssertionException("target is not remote-reference: " + invocation.getTarget());
        }

        RemoteIdentity remoteIdentity = (RemoteIdentity) invocation.getTarget();

        ServerConnectionManager connectionManager = remoteIdentity.getConnectionManager();
        ServerConnection connection = null;
        try {
            connection = connectionManager.openConnection();
        } catch (IOException e) {
            throw new CouldNotConnectException("could not connect with " + connectionManager, e);
        }

        try {
            Call call = new AuthenticatedCall(invocation);

            MarshallingOutputStream output = null;
            try {
                output = new MarshallingOutputStream(connection.getOutputStream(), marshaller);
            } catch (IOException e) {
                throw new CouldNotConnectException(e);
            }
            output.writeObject(call);
            MarshallingInputStream input = new MarshallingInputStream(connection.getInputStream(), marshaller);
            Object result = marshaller.unmarshal(input.readObject());
            if (result instanceof ExceptionThrown) {
                ExceptionThrown exceptionThrown = (ExceptionThrown) result;
                Throwable throwable = exceptionThrown.getThrowable();
                if (throwable == null) {
                    throwable = new RuntimeException("Remote error but exception was null");
                }
                throw throwable.fillInStackTrace();
            }
            return result;
        } finally {
            connection.close();
        }
    }

    void setMarshaller(Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    public void introduce(AspectInstance aspectInstance) {
    }

    public void advise(AspectInstance aspectInstance) {
        aspectInstance.addInterceptor(this);
    }
}
