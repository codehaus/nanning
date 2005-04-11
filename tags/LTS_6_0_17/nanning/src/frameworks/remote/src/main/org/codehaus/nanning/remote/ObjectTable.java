package org.codehaus.nanning.remote;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.nanning.AssertionException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Collections;

class ObjectTable {
    private static final Log logger = LogFactory.getLog(ObjectTable.class);

    private static final int DEFAULT_TIMEOUT = 10 * 60 * 60 * 1000;

    private Map objectToId = Collections.synchronizedMap(new HashMap());
    private Map idToReference = Collections.synchronizedMap(new HashMap());

    private long currentId = 0;
    private long timeout;

    public ObjectTable() {
        this(DEFAULT_TIMEOUT);
    }

    public ObjectTable(long timeout) {
        this.timeout = timeout;
    }

    Object getFromID(Object id) {
        LocalReference o = (LocalReference) idToReference.get(id);
        if (o == null) {
            throw new AssertionException();
        }
        o.touch();
        cleanupStale();
        return o.getReferred();
    }

    private synchronized void cleanupStale() {
        try {
            for (Iterator i = idToReference.values().iterator(); i.hasNext();) {
                LocalReference ref = (LocalReference) i.next();
                if (ref.isStale(timeout)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Removing " + ref.getReferred());
                    }
                    objectToId.remove(ref.getReferred());
                    i.remove();
                }
            }
        } catch (Exception ignore) {
            logger.warn("Got error while cleaning out stale objects (ignored)", ignore);
        }
    }

    synchronized Object register(Object o) {
        Object id = objectToId.get(o);
        if (id == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Registering " + o);
            }
            id = newId();
            objectToId.put(o, id);
            idToReference.put(id, new LocalReference(o));
        }
        return id;
    }

    private Object newId() {
        return new Long(currentId++);
    }

    public boolean hasID(Object o) {
        return objectToId.containsKey(o);
    }

    boolean isIDRegistered(Object id) {
        cleanupStale();
        return idToReference.containsKey(id);
    }

    public synchronized void clear() {
        if (logger.isDebugEnabled()) {
            logger.debug("Clearing");
        }
        objectToId.clear();
        idToReference.clear();
        currentId = 0;
    }

    private static class LocalReference {
        private Object object;
        private long lastTimeTouched;

        LocalReference(Object object) {
            this.object = object;
            touch();
        }

        Object getReferred() {
            return object;
        }

        void touch() {
            lastTimeTouched = System.currentTimeMillis();
        }

        boolean isStale(long timeout) {
            return System.currentTimeMillis() - lastTimeTouched >= timeout;
        }
    }
}
