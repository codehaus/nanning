package org.codehaus.nanning.prevayler;

import java.util.Collection;

import org.prevayler.util.clock.ClockedSystem;
import org.codehaus.nanning.prevayler.MyObject;


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
