package com.tirsen.nanning.samples.prevayler;

import java.io.*;

import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.config.AspectSystem;
import com.tirsen.nanning.config.ClassIntroductor;
import junit.framework.TestCase;

public class BasicIdentifyingSystemTest extends TestCase {
    private BasicIdentifyingSystem basicIdentifyingSystem;
    private Object registredObject;
    
    public static interface Interface {}
    public static class Implementation implements Interface, Serializable {}

    protected void setUp() throws Exception {
        super.setUp();

        AspectSystem aspectSystem = new AspectSystem();
        aspectSystem.addAspect(new ClassIntroductor(Interface.class, Implementation.class));
        Aspects.setContextAspectFactory(aspectSystem);

        registredObject = Aspects.getCurrentAspectFactory().newInstance(Interface.class);

        basicIdentifyingSystem = new BasicIdentifyingSystem();

        CurrentPrevayler.enterTransaction();
        basicIdentifyingSystem.registerObjectID(registredObject);
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
        Object readObject = readSystem.getAllRegisteredObjects().iterator().next();
        assertTrue(readSystem.hasObjectID(readObject));
        assertEquals(basicIdentifyingSystem.getObjectID(registredObject), readSystem.getObjectID(readObject));
        assertSame(readObject, readSystem.getObjectWithID(readSystem.getObjectID(readObject)));
    }
}
