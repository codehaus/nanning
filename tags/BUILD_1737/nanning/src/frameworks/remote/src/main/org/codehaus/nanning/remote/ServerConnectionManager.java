package org.codehaus.nanning.remote;


import org.codehaus.nanning.remote.ServerConnection;

import java.io.IOException;

public interface ServerConnectionManager {
    ServerConnection openConnection() throws IOException;
}
