package com.tirsen.nanning.remote;

import java.util.Collection;

/**
 * @remote
 * @service
 */
public interface MyStatelessService {
    /**
     * @transaction
     */
    void createObject(String attributeValue);
    
	public MyObject getMyObject();
	
	public Collection getAllObjects();

    void authenticatedCall(String expectedUserName);
}
