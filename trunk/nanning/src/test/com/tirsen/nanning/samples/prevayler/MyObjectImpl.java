package com.tirsen.nanning.samples.prevayler;

public class MyObjectImpl implements MyObject {
    private String attribute;
    private MyObject myObject;

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public void setMyObject(MyObject myObject) {
        this.myObject = myObject;
    }

    public MyObject getMyObject() {
        return myObject;
    }
}
