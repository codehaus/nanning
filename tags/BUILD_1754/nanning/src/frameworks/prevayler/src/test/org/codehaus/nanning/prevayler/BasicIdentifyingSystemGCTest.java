package org.codehaus.nanning.prevayler;

import org.codehaus.nanning.prevayler.BasicIdentifyingSystem;
import org.codehaus.nanning.prevayler.CurrentPrevayler;
import org.codehaus.nanning.test.TestUtils;
import junit.framework.TestCase;

public class BasicIdentifyingSystemGCTest extends TestCase {
    private BasicIdentifyingSystem basicIdentifyingSystem;
    private String registredObject;

    protected void setUp() throws Exception {
        super.setUp();

        basicIdentifyingSystem = new BasicIdentifyingSystem();
        registredObject = "BombingObject";
        for (int i = 0; i < 1024; i++) {
            registredObject += "I'm big";
        }
        CurrentPrevayler.enterTransaction(basicIdentifyingSystem);
        basicIdentifyingSystem.registerObjectID(registredObject);
        CurrentPrevayler.exitTransaction();
    }

    public void testGarbageCollection() {
        long objectID = basicIdentifyingSystem.getObjectID(registredObject);
        assertTrue(basicIdentifyingSystem.isIDRegistered(objectID));
        registredObject = null;

        TestUtils.gc();

        assertFalse("this test uses a trick to provoke a garbage collect of a specific object, " +
                    "in some situations it fails even if it's not necessarily an error", 
                basicIdentifyingSystem.isIDRegistered(objectID));
        assertTrue(basicIdentifyingSystem.hasNoRegisteredObjects());
    }
}
