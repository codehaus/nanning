package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.samples.prevayler.BasicIdentifyingSystem;
import com.tirsen.nanning.test.TestUtils;
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
        CurrentPrevayler.enterTransaction();
        basicIdentifyingSystem.registerObjectID(registredObject);
        CurrentPrevayler.exitTransaction();
    }

    public void testGarbageCollection() {
        long objectID = basicIdentifyingSystem.getObjectID(registredObject);
        assertTrue(basicIdentifyingSystem.isIDRegistered(objectID));
        registredObject = null;

        TestUtils.gc();

        assertFalse(basicIdentifyingSystem.isIDRegistered(objectID));
        assertTrue(basicIdentifyingSystem.hasNoRegisteredObjects());
    }
}
