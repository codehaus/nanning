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
        assert hasObjectID(object) : "object had no ID";
        return (long) objects.indexOf(object);
    }

    public boolean hasObjectID(Object object) {
        return objects.contains(object);
    }

    public long registerObjectID(Object object) {
        System.out.println("registering object " + object);
        assert !hasObjectID(object) : "already has ID";
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
