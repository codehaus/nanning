package com.tirsen.nanning.samples.rmi;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.MethodInterceptor;
import com.tirsen.nanning.config.InterceptorAdvise;
import com.tirsen.nanning.config.MethodPointcut;
import com.tirsen.nanning.config.PointcutAspect;
import com.tirsen.nanning.samples.prevayler.Call;
import com.tirsen.nanning.samples.prevayler.Marshaller;
import com.tirsen.nanning.samples.prevayler.MarshallingCall;

public class RemoteAspect extends PointcutAspect implements MethodInterceptor {
    private Marshaller marshaller;

    public RemoteAspect() {
        addPointcut(new MethodPointcut(new InterceptorAdvise(this)));
    }

    public Object invoke(Invocation invocation) throws Throwable {
        RemoteIdentity remoteIdentity = (RemoteIdentity) invocation.getTarget();

        ServerConnection connection = remoteIdentity.getConnectionManager().openConnection();

        try {
            Call call = new MarshallingCall(invocation, marshaller);

            ObjectOutputStream output = new ObjectOutputStream(connection.getOutputStream());
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
}
