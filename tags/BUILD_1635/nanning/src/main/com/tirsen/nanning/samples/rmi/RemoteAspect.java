package com.tirsen.nanning.samples.rmi;

import java.io.IOException;

import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.MethodInterceptor;
import com.tirsen.nanning.MixinInstance;
import com.tirsen.nanning.config.Aspect;
import com.tirsen.nanning.samples.prevayler.Call;
import com.tirsen.nanning.samples.prevayler.Marshaller;
import com.tirsen.nanning.samples.prevayler.AuthenticatedCall;

public class RemoteAspect implements Aspect, MethodInterceptor {
    private Marshaller marshaller;

    public Object invoke(Invocation invocation) throws Throwable {
        assert invocation.getTarget() instanceof RemoteIdentity :
                "target is not remote-reference: " + invocation.getTarget();
        
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

    public void adviseMixin(AspectInstance aspectInstance, MixinInstance mixin) {
        mixin.addInterceptor(this);
    }

    public void advise(AspectInstance aspectInstance) {
    }
}
