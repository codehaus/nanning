package com.tirsen.nanning.samples.prevayler;

import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;

import com.tirsen.nanning.Aspects;
import org.prevayler.util.clock.AbstractClockedSystem;

public class MySystemImpl extends AbstractClockedSystem implements MySystem, Serializable {
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

    public Collection getAllObjects() {
        Collection result = new ArrayList();
        addAllObjects(result, myObject);
        return result;
    }

    private void addAllObjects(Collection result, MyObject myObject) {
        if (myObject == null) {
            return;
        }
        result.add(myObject);
        addAllObjects(result, myObject.getMyObject());
    }
}
