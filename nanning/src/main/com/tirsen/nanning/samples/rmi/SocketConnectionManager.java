package com.tirsen.nanning.samples.rmi;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.Socket;

public class SocketConnectionManager implements ServerConnectionManager {
    private String host;
    private int port;

    public SocketConnectionManager(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public ServerConnection openConnection() throws IOException {
        return new SocketConnection(host, port);
    }

    public static class SocketConnection implements ServerConnection {
        private Socket socket;

        public SocketConnection(String host, int port) throws IOException {
            this.socket = new Socket(host, port);
        }

        public OutputStream getOutputStream() throws IOException {
            return socket.getOutputStream();
        }

        public InputStream getInputStream() throws IOException {
            return socket.getInputStream();
        }

        public void close() throws IOException {
            if (socket != null) {
                socket.close();
                socket = null;
            }
        }
    }
}
