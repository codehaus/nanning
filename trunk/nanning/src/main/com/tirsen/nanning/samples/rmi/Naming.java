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

        ServerConnection serverConnection = null;
        try {
            serverConnection = connectionManager.openConnection();
        } catch (IOException e) {
            throw new CouldNotConnectException(e);
        }
        try {
            ObjectOutputStream output = null;
            try {
                output = new ObjectOutputStream(serverConnection.getOutputStream());
            } catch (IOException e) {
                throw new CouldNotConnectException(e);
            }
            output.writeObject(new NamingLookup(name));
            ObjectInputStream input = null;
            try {
                input = new ObjectInputStream(serverConnection.getInputStream());
            } catch (IOException e) {
                throw new CommunicationException(e);
            }
            return marshaller.unmarshal(input.readObject());
        } finally {
            serverConnection.close();
        }
    }
}
