package com.tirsen.nanning.samples.rmi;


import java.io.IOException;

public interface ServerConnectionManager {
    ServerConnection openConnection() throws IOException;
}
