package org.codehaus.nanning.prevayler;

import java.util.Collection;

public interface IdentifyingSystem {
    Identifiable getIdentifiable(long oid);

    boolean hasNoRegisteredObjects();

    Collection getAllRegisteredObjects();

    boolean isIDRegistered(long objectId);

    long register(Object object);
}
