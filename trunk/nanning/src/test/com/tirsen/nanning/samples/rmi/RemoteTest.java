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
import org.prevayler.PrevalentSystem;
import org.prevayler.implementation.SnapshotPrevayler;

public class RemoteTest extends AbstractAttributesTest {
    private AspectSystem serverAspectSystem;
    private AspectSystem clientAspectSystem;
    private File prevaylerDir;
    private int port = 12346;
    private SnapshotPrevayler prevayler;
    private RemoteCallServer remoteCallServer;
    private String hostname = "localhost";
    private RemoteMarshaller marshaller;

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

        clientAspectSystem = new AspectSystem();
        clientAspectSystem.addAspect(new MixinAspect(MyStatelessService.class, null));
        clientAspectSystem.addAspect(new MixinAspect(MyStatefulService.class, MyStatefulServiceImpl.class));
        RemoteAspect remoteAspect = new RemoteAspect();
        remoteAspect.setHostname(hostname);
        remoteAspect.setPort(port);
        marshaller = new RemoteMarshaller(clientAspectSystem);
        remoteAspect.setMarshaller(marshaller);
        clientAspectSystem.addAspect(remoteAspect);

        // init server side
        Aspects.setContextAspectFactory(serverAspectSystem);
        prevayler = new SnapshotPrevayler((PrevalentSystem) serverAspectSystem.newInstance(MySystem.class),
                                                                    prevaylerDir.getAbsolutePath());

        remoteCallServer = new RemoteCallServer();
        remoteCallServer.setPort(port);
        remoteCallServer.setAspectFactory(serverAspectSystem);

        CurrentPrevayler.withPrevayler(prevayler, new Runnable() {
            public void run() {
                // start server, server-threads will inherit the prevayler and the context-aspect-factory from this thread
                remoteCallServer.start();
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
        MyStatelessService myService = (MyStatelessService) new Naming(marshaller, hostname, port).lookup("MyStatelessService");
        myService.createObject("attributeValue");

        // server side
        CurrentPrevayler.withPrevayler(prevayler, new Runnable() {
            public void run() {
                MySystem system = (MySystem) CurrentPrevayler.getSystem();

                // server side
                Collection objects = system.getAllRegisteredObjects();
                assertEquals("object not created on server side", 2, objects.size());
                MyObject myObject = (MyObject) CollectionUtils.find(objects, new Predicate() {
                    public boolean evaluate(Object o) {
                        return o instanceof MyObject;
                    }
                });
                assertEquals("attribute wrong value", "attributeValue", myObject.getValue());
            }
        });
    }

    public void testStatefulRemoteCall() throws IOException, ClassNotFoundException {
        // server side
        remoteCallServer.bind("MyStatefulService", serverAspectSystem.newInstance(MyStatefulService.class));

        // client side
        MyStatefulService statefulService = (MyStatefulService) new Naming(marshaller, hostname, port).lookup("MyStatefulService");
        assertNull(statefulService.value());
        statefulService.modify("value");
        assertEquals("value", statefulService.value());
    }

    public void testObjectTable() {
        Object o = new Object();
        RemoteMarshaller objectTable = new RemoteMarshaller();
        Object id = objectTable.registerID(o);
        assertTrue(objectTable.isIDRegistered(id));
        assertSame(o, objectTable.getFromID(id));
        assertSame(id, objectTable.registerID(o));
    }

    public void testRemoteMarshallerWithLocalObject() {
        RemoteMarshaller remoteMarshaller = new RemoteMarshaller();
        MyStatefulServiceImpl o = new MyStatefulServiceImpl();
        Object id = remoteMarshaller.registerID(o);
        assertSame(o, remoteMarshaller.unmarshal(new Identity(o.getClass(), id)));
    }

    public void testIdentity() {
        Identity identity1 = new Identity(Object.class, new Long(1));
        Identity identity2 = new Identity(Object.class, new Long(1));
        assertEquals(identity1, identity2);
    }

    public void testRemoteMarshallerWithRemoteObject() {
        Object service = serverAspectSystem.newInstance(MyStatefulService.class);

        RemoteMarshaller remoteMarshaller = new RemoteMarshaller(clientAspectSystem);
        Identity identity = new Identity((Class) Aspects.getAspectInstance(service).getClassIdentifier(), new Long(System.currentTimeMillis()));
        Object stub = remoteMarshaller.unmarshal(identity);
        assertNotNull(stub);
        assertTrue(stub instanceof MyStatefulService);
        assertTrue(Aspects.getAspectInstance(stub).getTargets()[0] instanceof Identity);
        assertEquals(identity, Aspects.getAspectInstance(stub).getTargets()[0]);
        assertEquals(identity, remoteMarshaller.marshal(stub));
    }
}
