package com.tirsen.nanning.samples.prevayler;

import java.util.Collection;


/**
 * @entity
 */
public interface MySystem {
    /**
     * @transaction
     */
    MyObject createMyObject();

    /**
     * @transaction
     */
    void setMyObject(MyObject myObject);

    MyObject getMyObject();

    Collection getAllObjects();
}
