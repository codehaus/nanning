package com.tirsen.nanning.remote;

/**
 * @service
 */
public interface MyService {
    /**
     * @transaction
     */
    MyObject createObject(String attributeValue);
}
