package com.tirsen.nanning.remote;

import java.util.Collection;

import org.prevayler.util.clock.ClockedSystem;


/**
 * @entity
 */
public interface MySystem extends ClockedSystem {
    MyObject createMyObject();

    /**
     * @transaction
     */
    void setMyObject(MyObject myObject);

    MyObject getMyObject();

    Collection getAllObjects();
}
