package com.tirsen.nanning.samples.rmi;

import java.io.*;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.AspectFactory;
import com.tirsen.nanning.samples.prevayler.Call;
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

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(1000);
            threadPool = new DefaultThreadPool(threadPoolSize);

            Thread acceptThread = new Thread(new Runnable() {
                public void run() {
                    while (!doStop) {
                        try {
                            final Socket socket = serverSocket.accept();

                            threadPool.invokeLater(
                                    new Runnable() {
                                        public void run() {
                                            processCall(socket);
                                        }
                                    }
                            );

                        } catch (SocketTimeoutException ignore) {
                        } catch (IOException e) {
                            logger.error("error accepting call", e);
                        }
                    }
                }
            });
            acceptThread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processCall(Socket socket) {
        try {
            Aspects.setContextAspectFactory(aspectRepository);

            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            ObjectInputStream input = new ObjectInputStream(inputStream);
            Call call = (Call) input.readObject();
            Method method = call.getMethod();

            Object service = aspectRepository.newInstance(call.getClassIdentifier());
            Object result = method.invoke(service, call.getArgs());

            ObjectOutputStream output = new ObjectOutputStream(outputStream);
            output.writeObject(result);
            output.close();
        } catch (Exception e) {
            logger.error("error executing call", e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                logger.error("error closing socket", e);
            }
        }
    }

    public void setAspectRepository(AspectFactory aspectRepository) {
        this.aspectRepository = aspectRepository;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void stop() {
        doStop = true;
    }
}
