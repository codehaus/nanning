package com.tirsen.nanning.samples.prevayler;

import java.io.Serializable;

/**
 * @entity
 */
public interface MyObject extends Serializable, FinalizationCallback {
    String getValue();

    /**
     * @transaction
     */
    void setValue(String attribute);

    /**
     * @transaction
     */
    void setMyObject(MyObject myObject);

    MyObject getMyObject();

    boolean wasFinalized();

    /**
     * @transaction
     */
    void setABC(String[] abc);
}
