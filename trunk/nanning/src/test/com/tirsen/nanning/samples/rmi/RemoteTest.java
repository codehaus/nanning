package com.tirsen.nanning.samples.rmi;

import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.attribute.AbstractAttributesTest;
import com.tirsen.nanning.definition.AspectClass;
import com.tirsen.nanning.definition.AspectRepository;
import com.tirsen.nanning.definition.InterceptorDefinition;
import com.tirsen.nanning.samples.prevayler.*;
import org.prevayler.PrevalentSystem;
import org.prevayler.implementation.SnapshotPrevayler;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RemoteTest extends AbstractAttributesTest {
    private AspectRepository serverAspectRepository;
    private AspectRepository clientAspectRepository;
    private File prevaylerDir;
    private int port = 12345;

    protected void setUp() throws Exception {
        super.setUp();
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
        {
            AspectClass aspectClass = new AspectClass();
            aspectClass.setInterface(MySystem.class);
            aspectClass.addInterceptor(serverAspectRepository.getInterceptor(PrevaylerInterceptor.class));
            aspectClass.setTarget(MySystemImpl.class);
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
        Aspects.setContextAspectFactory(serverAspectRepository);
        SnapshotPrevayler prevayler = new SnapshotPrevayler((PrevalentSystem) serverAspectRepository.newInstance(MySystem.class),
                        prevaylerDir.getAbsolutePath());
        CurrentPrevayler.setPrevayler(prevayler);
        CurrentPrevayler.withPrevayler(prevayler, new Runnable() {
            public void run() {
                MySystem system = (MySystem) CurrentPrevayler.getSystem();

                RemoteCallServer remoteCallServer = new RemoteCallServer();
                remoteCallServer.setPort(port);
                remoteCallServer.setAspectRepository(serverAspectRepository);
                remoteCallServer.start();

                // client side
                MyService myService = (MyService) clientAspectRepository.newInstance(MyService.class);
                MyObject myObject = myService.createObject("attributeValue");
                assertEquals("attribute wrong value", "attributeValue", myObject.getAttribute());

                // server side
                List objects = system.getObjects();
                assertEquals("object not created on server side", 2, objects.size());
                assertEquals("attribute wrong value", "attributeValue", ((MyObject) objects.get(1)).getAttribute());

                remoteCallServer.stop();
            }
        });
    }
}