package com.tirsen.nanning.samples.prevayler;

import java.util.List;

/**
 * @entity
 */
public interface MySystem extends IdentifyingSystem {
    /**
     * @ensures hasObjectID(result)
     * @transaction
     */
    MyObject createMyObject();

    List getObjects();
}
