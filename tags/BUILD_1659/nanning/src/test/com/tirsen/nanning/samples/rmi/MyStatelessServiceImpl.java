package com.tirsen.nanning.samples.rmi;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import java.security.AccessController;
import java.security.Principal;

import javax.security.auth.Subject;

import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.samples.prevayler.CurrentPrevayler;
import com.tirsen.nanning.samples.prevayler.MyObject;
import com.tirsen.nanning.samples.prevayler.MySystem;
import junit.framework.Assert;

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

    public void authenticatedCall(String expectedUserName) {
        Subject subject = Subject.getSubject(AccessController.getContext());
        Assert.assertNotNull(subject);
        Set principals = subject.getPrincipals(MyPrincipal.class);
        Assert.assertEquals(1, principals.size());
        MyPrincipal principal = (MyPrincipal) principals.iterator().next();
        Assert.assertEquals(expectedUserName, principal.getName());
    }
}
