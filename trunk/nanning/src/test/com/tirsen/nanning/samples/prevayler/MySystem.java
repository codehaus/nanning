package com.tirsen.nanning.samples.prevayler;

import org.prevayler.PrevalentSystem;
import org.prevayler.AlarmClock;

import java.util.List;
import java.util.ArrayList;

import com.tirsen.nanning.AspectRepository;

public class MySystem implements PrevalentSystem {
    private AlarmClock clock;
    private List objects = new ArrayList();

    public void clock(AlarmClock alarmClock) {
        this.clock = alarmClock;
    }

    public AlarmClock clock() {
        return clock;
    }

    public List getObjects() {
        return objects;
    }

    public void addObject(Object o) {
        objects.add(o);
    }

    public int getOID(Object object) {
        return objects.indexOf(object);
    }

    public Object getObject(int oid) {
        return objects.get(oid);
    }
}