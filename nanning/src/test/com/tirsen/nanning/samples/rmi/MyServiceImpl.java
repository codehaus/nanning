package com.tirsen.nanning.samples.rmi;

import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.samples.prevayler.MyObject;

public class MyServiceImpl implements MyService {
    public MyObject createObject(String attributeValue) {
        MyObject myObject = (MyObject) Aspects.getCurrentAspectFactory().newInstance(MyObject.class);
        myObject.setAttribute(attributeValue);
        return myObject;
    }
}
