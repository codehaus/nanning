package com.tirsen.nanning.samples.rmi;

import com.tirsen.nanning.samples.prevayler.MyObject;
import com.tirsen.nanning.samples.prevayler.CurrentPrevayler;
import com.tirsen.nanning.definition.AspectRepository;
import com.tirsen.nanning.Aspects;

public class MyServiceImpl implements MyService {
    public MyObject createObject(String attributeValue) {
        MyObject myObject = (MyObject) Aspects.getCurrentAspectFactory().newInstance(MyObject.class);
        myObject.setAttribute(attributeValue);
        return myObject;
    }
}
