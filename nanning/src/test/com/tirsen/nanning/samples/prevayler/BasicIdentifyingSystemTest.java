package com.tirsen.nanning.samples.prevayler;

import java.io.*;

import com.tirsen.nanning.samples.prevayler.BasicIdentifyingSystem;
import junit.framework.TestCase;

public class BasicIdentifyingSystemTest extends TestCase {
    private BasicIdentifyingSystem basicIdentifyingSystem;
    private String registredObject;

    protected void setUp() throws Exception {
        super.setUp();

        basicIdentifyingSystem = new BasicIdentifyingSystem();
        registredObject = "TestObject";
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
