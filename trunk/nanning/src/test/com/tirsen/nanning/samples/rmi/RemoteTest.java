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
    private File prevaylerDir;
    private int port = 12346;
    private SnapshotPrevayler prevayler;
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

        clientMarshaller = new RemoteMarshaller();

        // init server side
        Aspects.setContextAspectFactory(serverAspectSystem);
        prevayler = new SnapshotPrevayler((PrevalentSystem) serverAspectSystem.newInstance(MySystem.class),
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
        MyStatefulService statefulService = (MyStatefulService) new Naming(clientMarshaller, new SocketConnectionManager("localhost", port)).lookup("MyStatefulService");
        assertNull(statefulService.value());
        statefulService.modify("value");
        assertEquals("value", statefulService.value());
    }

    public void testObjectTable() {
        Object o = new Object();
        ObjectTable objectTable = new ObjectTable();
        Object id = objectTable.register(o);
        assertTrue(objectTable.isIDRegistered(id));
        assertSame(o, objectTable.getFromID(id));
        assertSame(id, objectTable.register(o));
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

        RemoteMarshaller remoteMarshaller = new RemoteMarshaller();
        Identity identity = new RemoteIdentity((Class) Aspects.getAspectInstance(service).getClassIdentifier(), new Long(System.currentTimeMillis()), new SocketConnectionManager("localhost", port));
        Object stub = remoteMarshaller.unmarshal(identity);
        assertNotNull(stub);
        assertTrue(stub instanceof MyStatefulService);
        assertTrue(Aspects.getAspectInstance(stub).getTargets()[0] instanceof Identity);
        assertEquals(identity, Aspects.getAspectInstance(stub).getTargets()[0]);
        assertEquals(identity, remoteMarshaller.marshal(stub));
    }
}
