package com.tirsen.nanning.remote;

import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.prevayler.CurrentPrevayler;

public class MyServiceImpl implements MyService {
    public MyObject createObject(String attributeValue) {
        MyObject myObject = (MyObject) Aspects.getCurrentAspectFactory().newInstance(MyObject.class);
        ((MySystem) CurrentPrevayler.getSystem()).setMyObject(myObject);
        myObject.setValue(attributeValue);
        return myObject;
    }
}
