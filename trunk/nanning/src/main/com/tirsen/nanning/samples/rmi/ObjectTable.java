package com.tirsen.nanning.samples.rmi;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class ObjectTable {
    private static final int DEFAULT_TIMEOUT = 60 * 60 * 1000;

    private Map objectToId = new HashMap();
    private Map idToStampedObject = new HashMap();

    private long currentId = 0;
    private long timeout;

    public ObjectTable() {
        this(DEFAULT_TIMEOUT);
    }

    public ObjectTable(long timeout) {
        this.timeout = timeout;
    }

    Object getFromID(Object id) {
        TimeStampedObject o = (TimeStampedObject) idToStampedObject.get(id);
        assert o != null;
        o.touch();
        cleanupStale();
        return o.getObject();
    }

    private void cleanupStale() {
        for (Iterator i = idToStampedObject.values().iterator(); i.hasNext();) {
            TimeStampedObject stampedObject = (TimeStampedObject) i.next();
            if (stampedObject.isStale()) {
                objectToId.remove(stampedObject.getObject());
                i.remove();
            }
        }
    }

    Object register(Object o) {
        Object id = objectToId.get(o);
        if (id == null) {
            id = newId();
            objectToId.put(o, id);
            idToStampedObject.put(id, new TimeStampedObject(o, System.currentTimeMillis()));
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
        return idToStampedObject.containsKey(id);
    }

    public void clear() {
        objectToId.clear();
        idToStampedObject.clear();
        currentId = 0;
    }

    private class TimeStampedObject {
        private Object object;
        private long timeStamp;


        public TimeStampedObject(Object object, long timeStamp) {
            this.object = object;
            this.timeStamp = timeStamp;
        }

        public long getTimeStamp() {
            return timeStamp;
        }

        public Object getObject() {
            return object;
        }

        public void touch() {
            timeStamp = System.currentTimeMillis();
        }

        public boolean isStale() {
            return System.currentTimeMillis() - getTimeStamp() >= timeout;
        }
    }
}
