package com.tirsen.nanning.samples.rmi;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.attribute.AbstractAttributesTest;
import com.tirsen.nanning.config.AspectSystem;
import com.tirsen.nanning.config.MixinAspect;
import com.tirsen.nanning.samples.prevayler.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.prevayler.implementation.SnapshotPrevayler;

public class RemoteCallServerTest extends AbstractAttributesTest {
    private AspectSystem serverAspectSystem;
    private File prevaylerDir;
    private int port = 12346;
    private CountingPrevayler prevayler;
    private SocketRemoteCallServer remoteCallServer;
    private RemoteMarshaller clientMarshaller;

    protected void setUp() throws Exception {
        super.setUp();
        prevaylerDir = File.createTempFile("test", "");
        prevaylerDir.delete();
        prevaylerDir.deleteOnExit();
        prevaylerDir.mkdirs();

        serverAspectSystem = new AspectSystem();
        serverAspectSystem.addAspect(new MixinAspect(MyObject.class, MyObjectImpl.class));
        serverAspectSystem.addAspect(new MixinAspect(MySystem.class, MySystemImpl.class));
        serverAspectSystem.addAspect(new MixinAspect(MyStatelessService.class, MyStatelessServiceImpl.class));
        serverAspectSystem.addAspect(new MixinAspect(MyStatefulService.class, MyStatefulServiceImpl.class));
        serverAspectSystem.addAspect(new PrevaylerAspect());

        clientMarshaller = RemoteMarshaller.createClientSideMarshaller();

        // init server side
        Aspects.setContextAspectFactory(serverAspectSystem);
        prevayler = new CountingPrevayler(serverAspectSystem.newInstance(MySystem.class),
                                          prevaylerDir.getAbsolutePath());

        remoteCallServer = new SocketRemoteCallServer();
        remoteCallServer.setPort(port);

        CurrentPrevayler.withPrevayler(prevayler, new Runnable() {
            public void run() {
                // start server, server-threads will inherit the prevayler and the context-aspect-factory from this thread
                remoteCallServer.start();
                remoteCallServer.setAspectFactory(serverAspectSystem);
            }
        });
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        remoteCallServer.stop();
    }

    public void testStatelessRemoteCall() throws IOException, ClassNotFoundException {
        // server side
        remoteCallServer.bind("MyStatelessService", serverAspectSystem.newInstance(MyStatelessService.class));

        // client side
        MyStatelessService myService = (MyStatelessService) new Naming(clientMarshaller, new SocketConnectionManager("localhost", port)).lookup("MyStatelessService");
        myService.createObject("attributeValue");

        // server side
        CurrentPrevayler.withPrevayler(prevayler, new Runnable() {
            public void run() {
                MySystem system = (MySystem) CurrentPrevayler.getSystem();

                // server side
                assertNotNull("object not created on server side", system.getMyObject());
                assertEquals("attribute wrong value", "attributeValue", system.getMyObject().getValue());
            }
        });
    }

    public void testStatefulRemoteCall() throws IOException, ClassNotFoundException {
        // server side
        remoteCallServer.bind("MyStatefulService", serverAspectSystem.newInstance(MyStatefulService.class));

        // client side
        MyStatefulService statefulService = (MyStatefulService) new Naming(clientMarshaller, new SocketConnectionManager("localhost", port)).lookup("MyStatefulService");
        assertNull(statefulService.value());
        statefulService.modify("value");
        assertEquals("value", statefulService.value());
    }
}
