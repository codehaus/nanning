package com.tirsen.nanning.samples.prevayler;



/**
 * @entity
 */
public interface MySystem extends IdentifyingSystem {
    /**
     * @ensures hasObjectID(result)
     * @transaction
     */
    MyObject createMyObject();

    /**
     * @transaction
     */
    void setMyObject(MyObject myObject);

    MyObject getMyObject();
}
