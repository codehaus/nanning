package com.tirsen.nanning.samples.rmi;

import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.MethodInterceptor;
import com.tirsen.nanning.definition.SingletonInterceptor;
import com.tirsen.nanning.samples.prevayler.Call;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RemoteInterceptor implements MethodInterceptor, SingletonInterceptor {
    private String hostname = "localhost";
    private int port = 4711;

    public void setPort(int port) {
        this.port = port;
    }

    public Object invoke(Invocation invocation) throws Throwable {
        Socket socket = new Socket(hostname, port);
        try {
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            Call call = new RemoteServiceCall(invocation);
            output.writeObject(call);
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            return input.readObject();
        } finally {
            socket.close();
        }
    }
}