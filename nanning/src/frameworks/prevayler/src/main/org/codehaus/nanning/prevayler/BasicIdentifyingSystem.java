package org.codehaus.nanning.prevayler;

import java.util.*;
import java.io.Serializable;

//import org.prevayler.util.clock.AbstractClockedSystem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.nanning.AssertionException;

public class BasicIdentifyingSystem implements IdentifyingSystem, Serializable {
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
        if (object == null) {
            throw new AssertionException("could not find object with id " + oid);
        }
        return object;
    }

    public synchronized long getObjectID(Object object) {
        Long aLong = (Long) objectToId.get(object);
        if (aLong == null) {
            throw new AssertionException("object " + object + " had no object id, use registerObjectID(Object)");
        }
        return aLong.longValue();
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

        if (object == null) {
            throw new AssertionException("can't register null");
        }
        if (hasObjectID(object)) {
            throw new AssertionException("already has ID: " + object);
        }

        Long id = getNextId();
        if (isIDRegistered(id.longValue())) {
            throw new AssertionException();
        }

        idToObject.put(id, object);
        objectToId.put(object, id);

        logger.debug("registering object " + object + " with id " + id);

        return id.longValue();
    }

    private synchronized Long getNextId() {
        return new Long(nextObjectId++);
    }
}
