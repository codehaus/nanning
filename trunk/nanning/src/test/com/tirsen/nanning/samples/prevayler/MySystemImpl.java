package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.Aspects;

public class MySystemImpl extends BasicIdentifyingSystem implements MySystem {
    public MyObject createMyObject() {
        return (MyObject) Aspects.getCurrentAspectFactory().newInstance(MyObject.class);
    }
}
