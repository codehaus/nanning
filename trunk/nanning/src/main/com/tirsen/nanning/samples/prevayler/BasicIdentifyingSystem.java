package com.tirsen.nanning.samples.prevayler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.prevayler.AlarmClock;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class BasicIdentifyingSystem implements IdentifyingSystem {
    private static final Log logger = LogFactory.getLog(BasicIdentifyingSystem.class);
    private AlarmClock clock;
    protected List objects = new ArrayList();

    public void clock(AlarmClock alarmClock) {
        this.clock = alarmClock;
    }

    public AlarmClock clock() {
        return clock;
    }

    public List getObjects() {
        return objects;
    }

    public long getObjectID(Object object) {
        assert hasObjectID(object) : "object had no ID: " + object;
        return (long) objects.indexOf(object);
    }

    public boolean hasObjectID(Object object) {
        return objects.contains(object);
    }

    public void keepTheseObjects(Set objectsToKeep) {
        Set objectsToRemove = new HashSet(objects);
        objectsToRemove.removeAll(objectsToKeep);
        objects.removeAll(objectsToRemove);
    }

    public long registerObjectID(Object object) {
        logger.debug("registering object " + object);
        assert !hasObjectID(object) : "already has ID: " + object;
        objects.add(object);
        return (long) objects.indexOf(object);
    }

    public Object getObjectWithID(long oid) {
        Object object = objects.get((int) oid);
        assert object != null : "could not find object with id " + oid;
        assert hasObjectID(object);
        return object;
    }
}
