package com.tirsen.nanning.samples.rmi;

import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.samples.prevayler.MyObject;
import com.tirsen.nanning.samples.prevayler.CurrentPrevayler;
import com.tirsen.nanning.samples.prevayler.MySystem;

public class MyServiceImpl implements MyService {
    public MyObject createObject(String attributeValue) {
        MyObject myObject = (MyObject) Aspects.getCurrentAspectFactory().newInstance(MyObject.class);
        ((MySystem) CurrentPrevayler.getSystem()).setMyObject(myObject);
        myObject.setValue(attributeValue);
        return myObject;
    }
}
