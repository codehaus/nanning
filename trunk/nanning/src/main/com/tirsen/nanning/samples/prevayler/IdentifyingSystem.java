package com.tirsen.nanning.samples.prevayler;

import org.prevayler.PrevalentSystem;

public interface IdentifyingSystem extends PrevalentSystem {
    Object getObjectWithID(long oid);

    long getObjectID(Object object);
}
