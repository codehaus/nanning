package com.tirsen.nanning.samples.rmi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.tirsen.nanning.samples.prevayler.Marshaller;

public class Naming {
    private Marshaller marshaller;
    private String hostname;
    private int port;

    public Naming(Marshaller marshaller, String hostname, int port) {
        this.marshaller = marshaller;
        this.hostname = hostname;
        this.port = port;
    }

    public Object lookup(String name) throws IOException, ClassNotFoundException {
        Socket socket = new Socket(hostname, port);
        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
        output.writeObject(new NamingLookup(name));
        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
        return marshaller.unmarshal(input.readObject());
    }
}
