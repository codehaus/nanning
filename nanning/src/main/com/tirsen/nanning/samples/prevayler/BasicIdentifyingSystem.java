package com.tirsen.nanning.samples.prevayler;

import java.util.*;

import org.prevayler.util.clock.AbstractClockedSystem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BasicIdentifyingSystem extends AbstractClockedSystem implements IdentifyingSystem {
    private static final Log logger = LogFactory.getLog(BasicIdentifyingSystem.class);
    static final long serialVersionUID = 4503034161857395426L;

    private Map idToObject = SoftMap.createSoftValuesMap();
    /**
     * Rebuild this after xml-serialization.
     * @xml-serializer-transient
     */
    private Map objectToId = SoftMap.createSoftKeysMap();

    private long nextObjectId = 0;

    public void rebuildKeysMap() {
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

        Collection result = new ArrayList();
        for (Iterator i = objectToId.entrySet().iterator(); i.hasNext();) {
            try {
                Map.Entry entry = (Map.Entry) i.next();
                result.add(entry.getKey());
            } catch (NoSuchElementException ignore) {
                /* This sometimes happens with the jakarta ReferenceMap, lets just ignore it and continue */
            }
        }

        return result;
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
