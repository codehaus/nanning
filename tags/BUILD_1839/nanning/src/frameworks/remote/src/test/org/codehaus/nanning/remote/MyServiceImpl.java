package org.codehaus.nanning.remote;

import org.codehaus.nanning.Aspects;
import org.codehaus.nanning.prevayler.CurrentPrevayler;

public class MyServiceImpl implements MyService {
    public MyObject createObject(String attributeValue) {
        MyObject myObject = (MyObject) Aspects.getCurrentAspectFactory().newInstance(MyObject.class);
        ((MySystem) CurrentPrevayler.getSystem()).setMyObject(myObject);
        myObject.setValue(attributeValue);
        return myObject;
    }
}
