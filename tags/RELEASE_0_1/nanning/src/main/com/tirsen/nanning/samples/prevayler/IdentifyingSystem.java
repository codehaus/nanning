package com.tirsen.nanning.samples.prevayler;

import org.prevayler.PrevalentSystem;

public interface IdentifyingSystem extends PrevalentSystem {
    long getOID(Object object);

    Object getObjectWithID(long oid);

    long registerOID(Object object);
}
