package com.tirsen.nanning.remote;


import com.tirsen.nanning.remote.ServerConnection;

import java.io.IOException;

public interface ServerConnectionManager {
    ServerConnection openConnection() throws IOException;
}
