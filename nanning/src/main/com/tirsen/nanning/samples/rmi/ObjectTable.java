package com.tirsen.nanning.samples.rmi;

import java.util.Map;
import java.util.HashMap;

class ObjectTable {
    private Map objectToId = new HashMap();
    private Map idToObject = new HashMap();
    private long currentId = 0;

    boolean isIDRegistered(Object id) {
        return idToObject.containsKey(id);
    }

    Object getFromID(Object id) {
        Object o = idToObject.get(id);
        assert o != null;
        return o;
    }

    Object register(Object o) {
        Object id = objectToId.get(o);
        if (id == null) {
            id = newId();
            objectToId.put(o, id);
            idToObject.put(id, o);
        }
        return id;
    }

    private Object newId() {
        return new Long(currentId++);
    }

    public boolean hasID(Object o) {
        return objectToId.containsKey(o);
    }

    public void clear() {
        objectToId.clear();
        idToObject.clear();
        currentId = 0;
    }
}
