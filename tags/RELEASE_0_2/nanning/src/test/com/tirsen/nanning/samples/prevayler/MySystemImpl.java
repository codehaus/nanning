package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.Aspects;

public class MySystemImpl extends BasicIdentifyingSystem implements MySystem {
    private MyObject myObject;

    public MyObject createMyObject() {
        return (MyObject) Aspects.getCurrentAspectFactory().newInstance(MyObject.class);
    }

    public void setMyObject(MyObject myObject) {
        this.myObject = myObject;
    }

    public MyObject getMyObject() {
        return myObject;
    }
}
