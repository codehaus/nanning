package com.tirsen.nanning.samples.prevayler;

import java.util.Collection;

public interface IdentifyingSystem {
    Object getObjectWithID(long oid);

    long getObjectID(Object object);

    boolean hasNoRegisteredObjects();

    Collection getAllRegisteredObjects();

    boolean hasObjectID(Object object);

    boolean isIDRegistered(long objectId);

    long registerObjectID(Object object);
}
