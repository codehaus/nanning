package com.tirsen.nanning.samples.rmi;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;

public interface ServerConnection {
    OutputStream getOutputStream() throws IOException;

    InputStream getInputStream() throws IOException;

    void close() throws IOException;
}
