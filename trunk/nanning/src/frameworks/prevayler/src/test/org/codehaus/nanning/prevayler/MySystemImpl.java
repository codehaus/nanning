package org.codehaus.nanning.prevayler;

import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.nanning.Aspects;
import org.codehaus.nanning.prevayler.MyObject;
import org.codehaus.nanning.prevayler.MySystem;

public class MySystemImpl extends BasicIdentifyingSystem implements MySystem, Serializable {
    private MyObject myObject;
    private String simpleString;
    private List objects = new ArrayList();
    private Object simpleObject;

    public MyObject createMyObject() {
        return (MyObject) ((MySystem) Aspects.getThis()).newInstance(MyObject.class);
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

    public String getSimpleString() {
        return simpleString;
    }

    public void add(Object o) {
        objects.add(o);
    }

    public void setSimpleObject(Object o) {
        this.simpleObject = o;
    }

    public Object getSimpleObject() {
        return this.simpleObject;
    }

    public Object newInstance(Class mainClass) {
        return Aspects.getCurrentAspectFactory().newInstance(mainClass);
    }

    public void setSimpleString(String simpleString) {
        this.simpleString = simpleString;
    }

    public String changeAndReturnPreviousValue(PrevaylerTest.ObjectWithValue objectWithValue, String newValue) {
        String retval = objectWithValue.getValue();
        objectWithValue.setValue(newValue);
        return retval;
    }
}
