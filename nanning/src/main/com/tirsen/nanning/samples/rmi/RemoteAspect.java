package com.tirsen.nanning.samples.rmi;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.MethodInterceptor;
import com.tirsen.nanning.config.InterceptorAdvise;
import com.tirsen.nanning.config.MethodPointcut;
import com.tirsen.nanning.config.PointcutAspect;
import com.tirsen.nanning.samples.prevayler.Call;
import com.tirsen.nanning.samples.prevayler.MarshallingCall;
import com.tirsen.nanning.samples.prevayler.Marshaller;

public class RemoteAspect extends PointcutAspect implements MethodInterceptor {
    private Marshaller marshaller;

    public RemoteAspect() {
        addPointcut(new MethodPointcut(new InterceptorAdvise(this)));
    }

    public Object invoke(Invocation invocation) throws Throwable {
        RemoteIdentity remoteIdentity = (RemoteIdentity) invocation.getTarget();
        Socket socket = new Socket(remoteIdentity.getHostname(), remoteIdentity.getPort());
        try {
            Call call = new MarshallingCall(invocation, marshaller);

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(call);
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            Object result = marshaller.unmarshal(input.readObject());
            if (result instanceof ExceptionThrown) {
                ExceptionThrown exceptionThrown = (ExceptionThrown) result;
                throw exceptionThrown.getThrowable().fillInStackTrace();
            }
            return result;
        } finally {
            socket.close();
        }
    }

    void setMarshaller(Marshaller marshaller) {
        this.marshaller = marshaller;
    }
}
