package com.tirsen.nanning.samples.rmi;

import com.tirsen.nanning.samples.prevayler.MyObject;

/**
 * @service
 */
public interface MyService {
    /**
     * @transaction
     */
    MyObject createObject(String attributeValue);
}
