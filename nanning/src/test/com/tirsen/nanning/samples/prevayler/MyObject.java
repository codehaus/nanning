package com.tirsen.nanning.samples.prevayler;

import java.io.Serializable;

/**
 * @entity
 */
public interface MyObject extends Serializable, FinalizationCallback {
    String getAttribute();

    /**
     * @transaction
     */
    void setAttribute(String attribute);

    /**
     * @transaction
     */
    void setMyObject(MyObject myObject);

    MyObject getMyObject();

    boolean wasFinalized();
}
