package com.tirsen.nanning.samples.prevayler;

import org.prevayler.PrevalentSystem;

public interface IdentifyingSystem extends PrevalentSystem {
    int getOID(Object object);

    Object getObjectWithID(int oid);

    void registerOID(Object object);
}
