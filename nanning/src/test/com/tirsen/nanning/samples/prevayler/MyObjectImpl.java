package com.tirsen.nanning.samples.prevayler;

public class MyObjectImpl implements MyObject {
    private String attribute;

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MyObjectImpl)) return false;

        final MyObjectImpl myObject = (MyObjectImpl) o;

        if (attribute != null ? !attribute.equals(myObject.attribute) : myObject.attribute != null) return false;

        return true;
    }

    public int hashCode() {
        return (attribute != null ? attribute.hashCode() : 0);
    }
}
