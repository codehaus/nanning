package com.tirsen.nanning.samples.prevayler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.prevayler.AlarmClock;

import java.util.*;

public class BasicIdentifyingSystem implements IdentifyingSystem {
    static final long serialVersionUID = 4503034161857395426L;

    private static final Log logger = LogFactory.getLog(BasicIdentifyingSystem.class);
    private AlarmClock clock;

    private Map objectsById = new HashMap();
    private Map idsByObject = new IdentityHashMap();
    private int nextObjectId = 0;

    public void clock(AlarmClock alarmClock) {
        this.clock = alarmClock;
    }

    public AlarmClock clock() {
        return clock;
    }

    public Collection getAllRegisteredObjects() {
        return objectsById.values();
    }

    public long getObjectID(Object object) {
        assert hasObjectID(object) : "object had no ID: " + object;
        return ((Long) idsByObject.get(object)).longValue();
    }

    public boolean hasObjectID(Object object) {
        return idsByObject.containsKey(object);
    }

    public void unregisterObjectID(Object o) {
        Long id = (Long) idsByObject.remove(o);
        objectsById.remove(id);
    }

    public long registerObjectID(Object object) {
        logger.debug("registering object " + object);
        assert !hasObjectID(object) : "already has ID: " + object;

        Long id = getNextId();
        objectsById.put(id, object);
        idsByObject.put(object, id);
        return id.longValue();
    }

    private Long getNextId() {
        return new Long(nextObjectId++);
    }

    public Object getObjectWithID(long oid) {
        Object object = objectsById.get(new Long(oid));
        assert object != null : "could not find object with id " + oid;
        assert hasObjectID(object);
        return object;
    }
}
