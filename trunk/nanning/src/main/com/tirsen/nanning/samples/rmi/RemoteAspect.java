package com.tirsen.nanning.samples.rmi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;

import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.MethodInterceptor;
import com.tirsen.nanning.MixinInstance;
import com.tirsen.nanning.config.Aspect;
import com.tirsen.nanning.samples.prevayler.Call;
import com.tirsen.nanning.samples.prevayler.Marshaller;
import com.tirsen.nanning.samples.prevayler.MarshallingCall;

public class RemoteAspect implements Aspect, MethodInterceptor {
    private Marshaller marshaller;

    public boolean interceptsMethod(AspectInstance aspectInstance, MixinInstance mixin, Method method) {
        return true;
    }

    public Object invoke(Invocation invocation) throws Throwable {
        RemoteIdentity remoteIdentity = (RemoteIdentity) invocation.getTarget();

        ServerConnection connection = null;
        try {
            connection = remoteIdentity.getConnectionManager().openConnection();
        } catch (IOException e) {
            throw new CouldNotConnectException(e);
        }

        try {
            Call call = new MarshallingCall(invocation, marshaller);

            ObjectOutputStream output = null;
            try {
                output = new ObjectOutputStream(connection.getOutputStream());
            } catch (IOException e) {
                throw new CouldNotConnectException(e);
            }
            output.writeObject(call);
            ObjectInputStream input = new ObjectInputStream(connection.getInputStream());
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

    public Object introduce(AspectInstance aspectInstance) {
        return null;
    }

    public Object advise(AspectInstance aspectInstance, MixinInstance mixin) {
        return this;
    }

    public Object adviseConstruction(AspectInstance aspectInstance) {
        return null;
    }
}
