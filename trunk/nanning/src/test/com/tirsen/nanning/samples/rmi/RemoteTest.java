package com.tirsen.nanning.samples.rmi;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.tirsen.nanning.*;
import com.tirsen.nanning.samples.prevayler.*;
import junit.framework.TestCase;
import org.prevayler.implementation.SnapshotPrevayler;

public class RemoteTest extends TestCase {
    private AspectRepository serverAspectRepository;
    private AspectRepository clientAspectRepository;
    private File prevaylerDir;
    private int port = 4711;

    protected void setUp() throws Exception {
        super.setUp();
        AttributesTest.compileAttributes();
        prevaylerDir = File.createTempFile("test", "");
        prevaylerDir.delete();
        prevaylerDir.mkdirs();
    }

    public void testRemoteCall() throws IOException, ClassNotFoundException {
        serverAspectRepository = new AspectRepository();
        serverAspectRepository.defineInterceptor(new InterceptorDefinition(PrevaylerInterceptor.class));

        {
            AspectClass aspectClass = new AspectClass();
            aspectClass.setInterface(MyObject.class);
            aspectClass.addInterceptor(serverAspectRepository.getInterceptor(PrevaylerInterceptor.class));
            aspectClass.setTarget(MyObjectImpl.class);
            serverAspectRepository.defineClass(aspectClass);
        }

        {
            AspectClass aspectClass = new AspectClass();
            aspectClass.setInterface(MyService.class);
            aspectClass.addInterceptor(serverAspectRepository.getInterceptor(PrevaylerInterceptor.class));
            aspectClass.setTarget(MyServiceImpl.class);
            serverAspectRepository.defineClass(aspectClass);
        }

        clientAspectRepository = new AspectRepository();
        InterceptorDefinition remoteInterceptor = new InterceptorDefinition(RemoteInterceptor.class);
        ((RemoteInterceptor) remoteInterceptor.getSingleton()).setPort(port);
        clientAspectRepository.defineInterceptor(remoteInterceptor);

        {
            AspectClass aspectClass = new AspectClass();
            aspectClass.setInterface(MyObject.class);
            aspectClass.setTarget(MyObjectImpl.class);
            clientAspectRepository.defineClass(aspectClass);
        }

        {
            AspectClass aspectClass = new AspectClass();
            aspectClass.setInterface(MyService.class);
            aspectClass.addInterceptor(clientAspectRepository.getInterceptor(RemoteInterceptor.class));
            aspectClass.setTarget(null);
            clientAspectRepository.defineClass(aspectClass);
        }

        // init server side
        Aspects.setContextAspectRepository(serverAspectRepository);
        CurrentPrevayler.setPrevayler(new SnapshotPrevayler(new MySystem(), prevaylerDir.getAbsolutePath()));
        RemoteCallServer remoteCallServer = new RemoteCallServer();
        remoteCallServer.setPort(port);
        remoteCallServer.setAspectRepository(serverAspectRepository);
        remoteCallServer.start();

        // client side
        MyService myService = (MyService) clientAspectRepository.newInstance(MyService.class);
        MyObject myObject = myService.createObject("attributeValue");
        assertEquals("attribute wrong value", "attributeValue", myObject.getAttribute());

        // server side
        List objects = ((MySystem) CurrentPrevayler.getSystem()).getObjects();
        assertEquals("object not created on server side", 1, objects.size());
        assertEquals("attribute wrong value", "attributeValue", ((MyObject) objects.get(0)).getAttribute());

        remoteCallServer.stop();
    }
}
