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
    private String hostname = "localhost";
    private int port = 4711;
    private Marshaller marshaller;

    public RemoteAspect() {
        addPointcut(new MethodPointcut(new InterceptorAdvise(this)));
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Object invoke(Invocation invocation) throws Throwable {
        Socket socket = new Socket(hostname, port);
        try {
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            Call call = new MarshallingCall(invocation, marshaller);
            output.writeObject(call);
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            Object result = input.readObject();
            if (result instanceof ExceptionThrown) {
                ExceptionThrown exceptionThrown = (ExceptionThrown) result;
                throw exceptionThrown.getThrowable().fillInStackTrace();
            }
            return result;
        } finally {
            socket.close();
        }
    }

    public void setMarshaller(Marshaller marshaller) {
        this.marshaller = marshaller;
    }
}
