package org.codehaus.nanning.prevayler;

import java.io.*;

import org.codehaus.nanning.Aspects;
import org.codehaus.nanning.prevayler.BasicIdentifyingSystem;
import org.codehaus.nanning.prevayler.CurrentPrevayler;
import org.codehaus.nanning.config.AspectSystem;
import org.codehaus.nanning.config.MixinAspect;
import org.codehaus.nanning.config.P;
import junit.framework.TestCase;

public class BasicIdentifyingSystemTest extends TestCase {
    private BasicIdentifyingSystem basicIdentifyingSystem;
    private Identifiable registredObject;
    
    public static interface Interface {}
    public static class Implementation implements Interface, Serializable {}

    protected void setUp() throws Exception {
        super.setUp();

        AspectSystem aspectSystem = new AspectSystem();
        aspectSystem.addAspect(new MixinAspect(Interface.class, Implementation.class));
        aspectSystem.addAspect(new MixinAspect(Identifiable.class, IdentifiableImpl.class, P.all()));
        Aspects.setContextAspectFactory(aspectSystem);

        registredObject = (Identifiable) Aspects.getCurrentAspectFactory().newInstance(Interface.class);

        basicIdentifyingSystem = new BasicIdentifyingSystem();

        CurrentPrevayler.enterTransaction(basicIdentifyingSystem);
        basicIdentifyingSystem.register(registredObject);
        CurrentPrevayler.exitTransaction();
    }

    public void testSerialization() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(data);
        out.writeObject(basicIdentifyingSystem);
        out.close();

        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data.toByteArray()));
        BasicIdentifyingSystem readSystem = (BasicIdentifyingSystem) in.readObject();
        
        assertEquals(1, readSystem.getAllRegisteredObjects().size());
        Identifiable readObject = (Identifiable) readSystem.getAllRegisteredObjects().iterator().next();
        assertTrue(readObject.hasObjectID());
        assertEquals(registredObject.getObjectID(), (readObject.getObjectID()));
        assertSame(readObject, readSystem.getIdentifiable(readObject.getObjectID()));
    }
}
