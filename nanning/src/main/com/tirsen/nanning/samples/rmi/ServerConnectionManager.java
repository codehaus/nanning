package com.tirsen.nanning.samples.rmi;


import java.io.IOException;
import java.io.Serializable;

public interface ServerConnectionManager {
    ServerConnection openConnection() throws IOException;
}
