package com.tirsen.nanning.samples.rmi;

import java.io.*;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import com.tirsen.nanning.AspectFactory;
import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.samples.prevayler.MarshallingCall;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.threadpool.DefaultThreadPool;
import org.apache.commons.threadpool.ThreadPool;

public class RemoteCallServer {
    private static final Log logger = LogFactory.getLog(RemoteCallServer.class);

    private AspectFactory aspectRepository;
    private int port;
    private ServerSocket serverSocket;
    private ThreadPool threadPool;
    private int threadPoolSize = 5;
    private boolean doStop;
    private Thread serverThread;
    public static final int SERVER_SOCKET_TIMEOUT = 1000;
    private Map naming = new HashMap();
    private RemoteMarshaller marshaller = new RemoteMarshaller();

    public void start() {
        try {
            assert port != 0 : "port not specified";
            logger.info("starting RMI-server on port " + port);
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(SERVER_SOCKET_TIMEOUT);
            threadPool = new DefaultThreadPool(threadPoolSize);

            serverThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        while (!doStop) {
                            try {
                                Socket socket = serverSocket.accept();

                                threadPool.invokeLater(new CallProcessor(socket));

                            } catch (SocketTimeoutException ignore) {
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

    private void processCall(Socket socket) {
        Aspects.setContextAspectFactory(aspectRepository);

        try {
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            ObjectInputStream input = new ObjectInputStream(inputStream);
            Object command = input.readObject();

            Object result;

            if (command instanceof MarshallingCall) {
                MarshallingCall call = (MarshallingCall) command;

                result = processRemoteCall(call);

            } else if (command instanceof NamingLookup) {
                NamingLookup namingLookup = (NamingLookup) command;

                result = processNamingLookup(namingLookup);
            } else {
                result = new ExceptionThrown(new RuntimeException("No such command exception."));
            }

            result = marshaller.marshal(result);

            ObjectOutputStream output = new ObjectOutputStream(outputStream);
            output.writeObject(result);
            output.close();

        } catch (IOException e) {
            logger.error("error communicating with client", e);
        } catch (ClassNotFoundException e) {
            logger.error("error communicating with client", e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                logger.error("error closing socket", e);
            }
        }
    }

    private Object processNamingLookup(NamingLookup namingLookup) {
        Object result;
        String name = namingLookup.getName();
        result = naming.get(name);
        return result;
    }

    private Object processRemoteCall(MarshallingCall call) {
        Object result;
        try {
            call.setMarshaller(marshaller);
            Method method = call.getMethod();

            Object target = call.getTarget();
            result = method.invoke(target, call.getArgs());
        } catch (Throwable e) {
            logger.error("error executing call", e);
            result = new ExceptionThrown(e);
        }
        return result;
    }

    public void setAspectFactory(AspectFactory aspectRepository) {
        this.aspectRepository = aspectRepository;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void stop() {
        doStop = true;
        try {
            serverThread.join(SERVER_SOCKET_TIMEOUT + 1);
        } catch (InterruptedException e) {
            logger.warn("could not stop server properly");
        }
    }

    public void bind(String name, Object o) {
        naming.put(name, o);
    }

    private class CallProcessor implements Runnable {
        private final Socket socket;

        public CallProcessor(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                processCall(socket);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
