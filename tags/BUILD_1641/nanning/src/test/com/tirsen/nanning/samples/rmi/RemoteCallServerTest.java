package com.tirsen.nanning.samples.rmi;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashSet;
import java.security.PrivilegedExceptionAction;
import java.security.AccessController;
import java.security.PrivilegedActionException;

import javax.security.auth.Subject;

import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.attribute.AbstractAttributesTest;
import com.tirsen.nanning.config.AspectSystem;
import com.tirsen.nanning.config.FindTargetMixinAspect;
import com.tirsen.nanning.samples.prevayler.CurrentPrevayler;
import com.tirsen.nanning.samples.prevayler.MySystem;
import com.tirsen.nanning.samples.prevayler.PrevaylerAspect;
import org.prevayler.implementation.SnapshotPrevayler;
import com.tirsen.nanning.samples.prevayler.MyObject;

public class RemoteCallServerTest extends AbstractAttributesTest {
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
        serverAspectSystem.addAspect(new FindTargetMixinAspect());
        serverAspectSystem.addAspect(new PrevaylerAspect());

        clientMarshaller = RemoteMarshaller.createClientSideMarshaller();

        // init server side
        Aspects.setContextAspectFactory(serverAspectSystem);
        prevayler = new SnapshotPrevayler(serverAspectSystem.newInstance(MySystem.class),
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

    public void testAuthenticatedCall() throws PrivilegedActionException {
        // server side
        remoteCallServer.bind("MyStatelessService", serverAspectSystem.newInstance(MyStatelessService.class));

        // client side
        Subject.doAs(new Subject(false, new HashSet(), new HashSet(), new HashSet()), new PrivilegedExceptionAction() {
            public Object run() throws Exception {
                Subject subject = Subject.getSubject(AccessController.getContext());
                subject.getPrincipals().add(new MyPrincipal("expectedUserName"));

                Naming naming = new Naming(clientMarshaller, new SocketConnectionManager("localhost", port));
                MyStatelessService myService = (MyStatelessService) naming.lookup("MyStatelessService");

                myService.authenticatedCall("expectedUserName");

                return null;
            }
        });
    }

    public void testStatelessRemoteCall() throws IOException, ClassNotFoundException {
        // server side
        remoteCallServer.bind("MyStatelessService", serverAspectSystem.newInstance(MyStatelessService.class));

        // client side
        Naming naming = new Naming(clientMarshaller, new SocketConnectionManager("localhost", port));
        MyStatelessService myService = (MyStatelessService) naming.lookup("MyStatelessService");

        {
            myService.createObject("attributeValue");
        }

        {
            MyObject myObject = myService.getMyObject();
            String value = myObject.getValue();
            assertEquals("attributeValue", value);
        }

        {
            Collection col = myService.getAllObjects();
            assertEquals(1, col.size());
            MyObject[] myObjects = (MyObject[])col.toArray(new MyObject[1]);
            MyObject myObject = myObjects[0];
            String value = myObject.getValue();
            assertEquals("attributeValue", value);
        }

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