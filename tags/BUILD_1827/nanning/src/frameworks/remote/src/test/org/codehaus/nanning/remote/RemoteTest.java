package org.codehaus.nanning.remote;

import org.codehaus.nanning.Aspects;
import org.codehaus.nanning.attribute.AbstractAttributesTest;
import org.codehaus.nanning.config.AspectSystem;
import org.codehaus.nanning.config.FindTargetMixinAspect;
import org.codehaus.nanning.prevayler.Identity;

public class RemoteTest extends AbstractAttributesTest {
    private int port = 12346;
    private SocketRemoteCallServer remoteCallServer;

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        if (remoteCallServer != null) {
            remoteCallServer.stop();
        }
    }

    public void testStartStop() {
        remoteCallServer = new SocketRemoteCallServer();
        remoteCallServer.setPort(port);
        assertFalse(remoteCallServer.isStarted());
        remoteCallServer.stop();
        assertFalse(remoteCallServer.isStarted());
        remoteCallServer.start();
        assertTrue(remoteCallServer.isStarted());
    }

    public void testObjectTable() {
        Object o = new Object();
        ObjectTable objectTable = new ObjectTable();
        Object id = objectTable.register(o);
        assertTrue(objectTable.isIDRegistered(id));
        assertSame(o, objectTable.getFromID(id));
        assertSame(id, objectTable.register(o));
    }

    public void testObjectTableCleanup() throws Exception {
        Object o = new Object();
        ObjectTable objectTable = new ObjectTable(200);
        Object id = objectTable.register(o);
        Thread.sleep(50);
        assertTrue(objectTable.isIDRegistered(id));
        Thread.sleep(151);
        assertFalse(objectTable.isIDRegistered(id));
    }

    public void testRemoteMarshallerWithLocalObject() {
        RemoteMarshaller clientMarshaller = RemoteMarshaller.createClientSideMarshaller();
        MyStatefulServiceImpl o = new MyStatefulServiceImpl();
        Object id = clientMarshaller.registerID(o);
        assertSame(o, clientMarshaller.unmarshal(new Identity(o.getClass(), id)));
    }

    public void testIdentityEqualsAndHashCode() {
        Identity identity1 = new Identity(Object.class, new Long(1));
        assertEquals(identity1, identity1);
        assertFalse(identity1.equals(""));
        assertFalse(identity1.equals(new Identity(Object.class, new Long(2))));

        Identity identity2 = new Identity(Object.class, new Long(1));
        assertEquals(identity1, identity2);
        assertEquals(identity2, identity1);
        assertEquals(identity1.hashCode(), identity2.hashCode());
    }

    public void testRemoteMarshallerWithRemoteObject() {
        assertTrue(RemoteMarshaller.isRemote(MyStatefulService.class));

        AspectSystem aspectSystem = new AspectSystem();
        aspectSystem.addAspect(new FindTargetMixinAspect());
        Object service = aspectSystem.newInstance(MyStatefulService.class);

        RemoteMarshaller clientMarshaller = RemoteMarshaller.createClientSideMarshaller();
        Identity identity = new RemoteIdentity(Aspects.getAspectInstance(service).getClassIdentifier(),
                new Long(System.currentTimeMillis()), new SocketConnectionManager("localhost", port));
        Object stub = clientMarshaller.unmarshal(identity);
        assertNotNull(stub);
        assertTrue(stub instanceof MyStatefulService);

        Object target = Aspects.getAspectInstance(stub).getTargets()[0];
        assertTrue(target instanceof Identity);
        assertEquals(identity, target);
        assertEquals(identity, clientMarshaller.marshal(stub));
    }
}
