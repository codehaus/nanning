package com.tirsen.nanning.samples.prevayler;

import java.util.List;
import java.util.ArrayList;

import org.prevayler.AlarmClock;

public class BasicIdentifyingSystem implements IdentifyingSystem {
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
        if(!objects.contains(object)) {
            objects.add(object);
        }
        return (long) objects.indexOf(object);
    }

    public Object getObjectWithID(long oid) {
        Object o = objects.get((int) oid);
        assert o != null : "could not find object with id " + oid;
        return o;
    }
}
