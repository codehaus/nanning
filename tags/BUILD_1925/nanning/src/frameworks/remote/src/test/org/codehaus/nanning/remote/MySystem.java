package org.codehaus.nanning.remote;

import java.util.Collection;

import org.codehaus.nanning.prevayler.IdentifyingSystem;


/**
 * @entity
 */
public interface MySystem extends IdentifyingSystem {
    MyObject createMyObject();

    /**
     * @transaction
     */
    void setMyObject(MyObject myObject);

    MyObject getMyObject();

    Collection getAllObjects();
}
