package com.tirsen.nanning.samples.prevayler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.prevayler.AlarmClock;

import java.util.*;

public class BasicIdentifyingSystem implements IdentifyingSystem {
    static final long serialVersionUID = 4503034161857395426L;

    private static final Log logger = LogFactory.getLog(BasicIdentifyingSystem.class);
    private AlarmClock clock;

    /**
     * @weak
     */
    private Map idToObject = new HashMap();
    /**
     * @weak
     */
    private Map objectToId = new IdentityHashMap();

    private long nextObjectId = 0;

    public void clock(AlarmClock alarmClock) {
        this.clock = alarmClock;
    }

    public AlarmClock clock() {
        return clock;
    }

    public Collection getAllRegisteredObjects() {
        return idToObject.values();
    }

    public long getObjectID(Object object) {
        assert hasObjectID(object) : "object had no ID: " + object;
        return ((Long) objectToId.get(object)).longValue();
    }

    public boolean hasObjectID(Object object) {
        return objectToId.containsKey(object);
    }

    public void unregisterObjectID(Object o) {
        assert hasObjectID(o) : "object is not registered";
        assert o != null;
        Long id = (Long) objectToId.remove(o);
        assert id != null;
        idToObject.remove(id);
    }

    public long registerObjectID(Object object) {
        assert object != null : "can't register null";
        assert !hasObjectID(object) : "already has ID: " + object;

        Long id = getNextId();
        assert !idToObject.containsKey(id);
        assert !objectToId.containsKey(object);
        assert id != null && object != null;
        idToObject.put(id, object);
        objectToId.put(object,  id);
        logger.debug("registering object " + object + " with id " + id);
        return id.longValue();
    }

    private Long getNextId() {
        return new Long(nextObjectId++);
    }

    public Object getObjectWithID(long oid) {
        Object object = idToObject.get(new Long(oid));
        assert object != null : "could not find object with id " + oid;
        assert hasObjectID(object);
        return object;
    }
}
