package com.tirsen.nanning.samples.rmi;

import java.util.Collection;
import com.tirsen.nanning.samples.prevayler.MyObject;

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
}
