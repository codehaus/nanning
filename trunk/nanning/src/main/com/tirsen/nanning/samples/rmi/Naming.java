package com.tirsen.nanning.samples.rmi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.tirsen.nanning.samples.prevayler.Marshaller;

public class Naming {
    private Marshaller marshaller;
    private ServerConnectionManager connectionManager;

    public Naming(Marshaller marshaller, ServerConnectionManager connectionManager) {
        this.marshaller = marshaller;
        this.connectionManager = connectionManager;
    }

    public Object lookup(String name) throws IOException, ClassNotFoundException {

        ServerConnection serverConnection = connectionManager.openConnection();
        try {
            ObjectOutputStream output = new ObjectOutputStream(serverConnection.getOutputStream());
            output.writeObject(new NamingLookup(name));
            ObjectInputStream input = new ObjectInputStream(serverConnection.getInputStream());
            return marshaller.unmarshal(input.readObject());
        } finally {
            serverConnection.close();
        }
    }
}
