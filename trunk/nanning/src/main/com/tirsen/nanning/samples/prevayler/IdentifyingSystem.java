package com.tirsen.nanning.samples.prevayler;

import org.prevayler.PrevalentSystem;

public interface IdentifyingSystem extends PrevalentSystem {
    Object getObjectWithID(long oid);

    /**
     * @requires hasObjectID(object)
     */
    long getObjectID(Object object);

    /**
     * @requires object != null
     * @ensures hasObjectID(object)
     * @ensures getObjectWithID(getObjectID(object)) == object
     */
    long registerObjectID(Object object);

    boolean hasObjectID(Object object);
}
