package com.tirsen.nanning.samples.rmi;


import java.io.IOException;
import java.io.Serializable;

public interface ServerConnectionManager extends Serializable {
    ServerConnection openConnection() throws IOException;
}
