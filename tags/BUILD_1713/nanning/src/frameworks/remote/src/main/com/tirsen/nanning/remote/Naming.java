package com.tirsen.nanning.remote;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.tirsen.nanning.prevayler.Marshaller;
import com.tirsen.nanning.remote.CommunicationException;
import com.tirsen.nanning.remote.CouldNotConnectException;

public class Naming {
    private Marshaller marshaller;
    private ServerConnectionManager connectionManager;

    public Naming(Marshaller marshaller, ServerConnectionManager connectionManager) {
        this.marshaller = marshaller;
        this.connectionManager = connectionManager;
    }

    public Object lookup(String name) throws IOException, ClassNotFoundException {

        ServerConnection serverConnection;
        try {
            serverConnection = connectionManager.openConnection();
        } catch (IOException e) {
            throw new CouldNotConnectException(e);
        }
        try {
            ObjectOutputStream output;
            try {
                output = new ObjectOutputStream(serverConnection.getOutputStream());
            } catch (IOException e) {
                throw new CouldNotConnectException(e);
            }
            output.writeObject(new NamingLookup(name));
            ObjectInputStream input;
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
