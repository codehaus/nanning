package org.codehaus.nanning.prevayler;

import java.util.Collection;

import org.codehaus.nanning.prevayler.MyObject;


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

    /**
     * @transaction
     */
    void setSimpleString(String string);

    String getSimpleString();

    /**
     * @transaction
     */
    String changeAndReturnPreviousValue(PrevaylerTest.ObjectWithValue objectWithValue, String newValue);

    /**
     * @transaction
     */
    void add(Object o);

    /**
     * @transaction
     */
    void setSimpleObject(Object o);

    Object getSimpleObject();

    /**
     * @transaction
     */
    Object newInstance(Class objectClass);
}