package org.codehaus.nanning.remote;

import java.io.Serializable;

/**
 * @remote
 * @entity
 */
public interface MyObject extends Serializable {
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
