package com.tirsen.nanning.remote;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.PrivilegedAction;
import java.util.HashSet;

import javax.security.auth.Subject;

import com.tirsen.nanning.AspectFactory;
import com.tirsen.nanning.remote.RemoteCallServer;
import com.tirsen.nanning.remote.SocketConnectionManager;
import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SocketRemoteCallServer {
    private static final Log logger = LogFactory.getLog(SocketRemoteCallServer.class);

    private RemoteCallServer remoteCallServer;
    private int port;
    private ServerSocket serverSocket;
    private PooledExecutor threadPool;
    private int threadPoolSize = 5;
    private boolean doStop;
    private Thread serverThread;
    public static final int SERVER_SOCKET_TIMEOUT = 1000;
    private SocketConnectionManager connectionManager;

    public void start() {
        try {
            assert port != 0 : "port not specified";
            logger.info("starting RMI-server on port " + port);
            serverSocket = new ServerSocket(port);
            connectionManager = new SocketConnectionManager(InetAddress.getLocalHost().getCanonicalHostName(), port);
            remoteCallServer = new RemoteCallServer(connectionManager);
            serverSocket.setSoTimeout(SERVER_SOCKET_TIMEOUT);
            threadPool = new PooledExecutor(threadPoolSize);

            serverThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        while (!doStop) {
                            try {
                                Socket socket = serverSocket.accept();

                                threadPool.execute(new SocketCallProcessor(socket));

                            } catch (SocketTimeoutException ignore) {
                            } catch (InterruptedException ignore) {
                            } catch (IOException e) {
                                logger.error("error accepting call", e);
                            }
                        }
                    } finally {
                        try {
                            serverSocket.close();
                        } catch (IOException e) {
                            logger.warn("could not close server", e);
                        }
                    }
                }
            });
            serverThread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void stop() {
        if (serverThread == null && serverSocket == null) {
            return;
        }

        doStop = true;
        try {
            serverThread.join(SERVER_SOCKET_TIMEOUT + 1);
            serverThread = null;
        } catch (InterruptedException e) {
            ///CLOVER:OFF
            logger.warn("could not stop server properly", e);
            ///CLOVER:ON
        }

        try {
            serverSocket.close();
            serverSocket = null;
        } catch (IOException e) {
            ///CLOVER:OFF
            logger.warn("could not stop server properly", e);
            ///CLOVER:ON
        }
    }

    public void bind(String name, Object object) {
        remoteCallServer.bind(name, object);
    }

    public void reset() {
        remoteCallServer.reset();
    }

    public void setAspectFactory(AspectFactory aspectFactory) {
        remoteCallServer.setAspectFactory(aspectFactory);
    }

    public boolean isStarted() {
        return serverSocket != null || serverThread != null;
    }

    protected class SocketCallProcessor implements Runnable {
        private final Socket socket;

        public SocketCallProcessor(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            // run completely unauthenticated
            Subject.doAs(new Subject(true, new HashSet(), new HashSet(), new HashSet()), new PrivilegedAction() {
                public Object run() {
                    try {
                        remoteCallServer.processCall(socket.getInputStream(), socket.getOutputStream());
                    } catch (IOException e) {
                        logger.error("error communicating with client", e);
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            logger.error("error closing socket", e);
                        }
                    }
                    return null;
                }
            });
        }
    }
}
