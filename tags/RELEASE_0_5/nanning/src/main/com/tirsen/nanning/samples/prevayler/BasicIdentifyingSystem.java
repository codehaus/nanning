package com.tirsen.nanning.samples.prevayler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.prevayler.util.clock.AbstractClockedSystem;

public class BasicIdentifyingSystem extends AbstractClockedSystem implements IdentifyingSystem {
    static final long serialVersionUID = 4503034161857395426L;

    private static final Log logger = LogFactory.getLog(BasicIdentifyingSystem.class);

    private Map idToObject = SoftMap.createSoftValuesMap();
    /**
     * If serializing this and then serializing the serialized object again it will fail with an OptionalDataException,
     * thus I need to make this transient and reconstruct it from idToObject again.
     */
    private transient Map objectToId = SoftMap.createSoftKeysMap();

    private long nextObjectId = 0;

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        buildMapsAfterDeserialization();
    }

    public void buildMapsAfterDeserialization() {
        objectToId = SoftMap.createSoftKeysMap();
        for (Iterator iterator = idToObject.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Long id = (Long) entry.getKey();
            Object o = entry.getValue();
            objectToId.put(o, id);
        }
    }

    public synchronized Object getObjectWithID(long oid) {
        Object object = idToObject.get(new Long(oid));
        assert object != null : "could not find object with id " + oid;
        assert hasObjectID(object) : "object is not registered " + object;
        return object;
    }

    public synchronized long getObjectID(Object object) {
        assert hasObjectID(object) : "object " + object + " had no object id, use registerObjectID(Object)";
        return ((Long) objectToId.get(object)).longValue();
    }

    public synchronized boolean hasNoRegisteredObjects() {
        return objectToId.isEmpty();
    }

    public synchronized Collection getAllRegisteredObjects() {
        return new ArrayList(objectToId.keySet());
    }

    public synchronized boolean hasObjectID(Object object) {
        return objectToId.containsKey(object);
    }

    public synchronized boolean isIDRegistered(long objectId) {
        return idToObject.containsKey(new Long(objectId));
    }

    public synchronized long registerObjectID(final Object object) {
        if (!CurrentPrevayler.isInTransaction()) {
            throw new IllegalStateException("You have to be inside a transaction to register objects");
        }
        
        assert object != null : "can't register null";
        assert !hasObjectID(object) : "already has ID: " + object;

        Long id = getNextId();
        assert !isIDRegistered(id.longValue());

        idToObject.put(id, object);
        objectToId.put(object, id);

        logger.debug("registering object " + object + " with id " + id);

        return id.longValue();
    }

    private synchronized Long getNextId() {
        return new Long(nextObjectId++);
    }
}
