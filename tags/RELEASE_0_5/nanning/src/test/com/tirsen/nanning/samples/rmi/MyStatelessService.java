package com.tirsen.nanning.samples.rmi;


/**
 * @remote
 * @service
 */
public interface MyStatelessService {
    /**
     * @transaction
     */
    void createObject(String attributeValue);
}
