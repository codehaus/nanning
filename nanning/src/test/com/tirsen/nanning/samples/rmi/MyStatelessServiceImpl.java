package com.tirsen.nanning.samples.rmi;

import java.io.Serializable;
import java.util.Collection;

import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.samples.prevayler.CurrentPrevayler;
import com.tirsen.nanning.samples.prevayler.MyObject;
import com.tirsen.nanning.samples.prevayler.MySystem;

public class MyStatelessServiceImpl implements MyStatelessService, Serializable {
    public void createObject(String attributeValue) {
        MyObject myObject = (MyObject) Aspects.getCurrentAspectFactory().newInstance(MyObject.class);
        ((MySystem) CurrentPrevayler.getSystem()).setMyObject(myObject);
        myObject.setValue(attributeValue);
    }
    
    public MyObject getMyObject() {
    	return ((MySystem)CurrentPrevayler.getSystem()).getMyObject();
    }
	
	public Collection getAllObjects() {
		return ((MySystem)CurrentPrevayler.getSystem()).getAllObjects();
	}
}
