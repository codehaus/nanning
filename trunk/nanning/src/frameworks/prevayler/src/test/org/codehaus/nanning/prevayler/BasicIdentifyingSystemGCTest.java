package org.codehaus.nanning.prevayler;

import java.util.Collection;
import java.io.*;

import junit.framework.TestCase;

public class BasicIdentifyingSystemGCTest extends TestCase {
    private BasicIdentifyingSystem system;
    private IdString object1;
    private IdString object2;

    protected void setUp() throws Exception {
        super.setUp();
        system = new BasicIdentifyingSystem();
        object1 = new IdString("Hello");
        object2 = new IdString("World");
    }

    public void testBasicMapOperations() {
        assertTrue(system.hasNoRegisteredObjects());
        registerInTransaction(object1);
        assertEquals(object1, system.getIdentifiable(object1.getObjectID()));
        assertTrue(system.isIDRegistered(object1.getObjectID()));
        assertFalse(system.hasNoRegisteredObjects());

        assertFalse(system.isIDRegistered(object2.getObjectID()));
        assertNull(system.getIdentifiable(object2.getObjectID()));

        registerInTransaction(object2);

        Collection values = system.getAllRegisteredObjects();
        assertEquals(2, values.size());
        assertTrue(values.contains(object2));
    }

    public void testSoftValues() throws InterruptedException {
        registerInTransaction(object1);
        registerInTransaction(object2);

        assertEquals(object1, system.getIdentifiable(object1.getObjectID()));

        long removedId = object1.getObjectID();
        object1 = null;

        somethingWasGCd(2000);
        assertFalse("Key for value was not removed after GC", system.isIDRegistered(removedId));
        assertNull("Value was not removed after GC", system.getIdentifiable(removedId));
        assertEquals(1, system.getAllRegisteredObjects().size());
    }

    public void testSerialization() throws Exception {
        registerInTransaction(object1);
        registerInTransaction(object2);
        BasicIdentifyingSystem clone = getSerializedMap();
        assertEquals(system.getAllRegisteredObjects(), clone.getAllRegisteredObjects());
        assertEquals(object2, clone.getIdentifiable(object2.getObjectID()));
        assertNotSame(object2, clone.getIdentifiable(object2.getObjectID()));

        long object1ID = object1.getObjectID();
        object1 = null;
        somethingWasGCd(2000);

        assertFalse(system.isIDRegistered(object1ID));
        assertTrue(system.isIDRegistered(object2.getObjectID()));
        clone = getSerializedMap();
        assertEquals(system.getAllRegisteredObjects(), clone.getAllRegisteredObjects());
    }

    private void registerInTransaction(Identifiable object) {
        CurrentPrevayler.enterTransaction(system);
        system.register(object);
        CurrentPrevayler.exitTransaction();
    }

   private BasicIdentifyingSystem getSerializedMap() throws IOException, ClassNotFoundException {
       ByteArrayOutputStream data = new ByteArrayOutputStream();
       ObjectOutputStream out = new ObjectOutputStream(data);
       out.writeObject(system);
       out.close();

       ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data.toByteArray()));
       BasicIdentifyingSystem readMap = (BasicIdentifyingSystem) in.readObject();
       in.close();
       in = null;
       out = null;
       return readMap;
    }

    private void somethingWasGCd(long timeout) throws InterruptedException {
        long timePassed = 0;
        while (!system.hasBeenGCdSinceLastCall()) {
            System.gc();
            Thread.sleep(50);
            timePassed += 50;
            if (timePassed > timeout) {
                fail("Garbage collect did not occur within timeout");
            }
        }
    }

    private static class IdString implements Identifiable, Serializable {
        private String s;
        private long id = -1;

        public IdString(String s) {
            this.s = s;
        }

        public long getObjectID() {
            return id;
        }

        public boolean hasObjectID() {
            return id != -1;
        }

        public void setObjectID(long objectID) {
            id = objectID;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof IdString)) return false;

            final IdString idString = (IdString) o;

            if (id != idString.id) return false;
            if (s != null ? !s.equals(idString.s) : idString.s != null) return false;

            return true;
        }

        public int hashCode() {
            int result;
            result = (s != null ? s.hashCode() : 0);
            result = 29 * result + (int) (id ^ (id >>> 32));
            return result;
        }
    }
}
