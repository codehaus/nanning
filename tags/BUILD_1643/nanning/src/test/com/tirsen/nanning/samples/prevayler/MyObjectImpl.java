package com.tirsen.nanning.samples.prevayler;

public class MyObjectImpl implements MyObject {
    private String value;
    private MyObject myObject;
    private boolean wasFinalized;

    public String getValue() {
        return value;
    }

    public void setValue(String attribute) {
        this.value = attribute;
    }

    public void setMyObject(MyObject myObject) {
        this.myObject = myObject;
    }

    public MyObject getMyObject() {
        return myObject;
    }

    public boolean wasFinalized() {
        return wasFinalized;
    }

    public void setABC(String[] abc) {
    }

    public void finalizationCallback() {
        this.wasFinalized = true;
    }
}
