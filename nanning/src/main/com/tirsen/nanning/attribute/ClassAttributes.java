package com.tirsen.nanning.attribute;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ClassAttributes {
    private Class aClass;

    public ClassAttributes(Class aClass) {
        this.aClass = aClass;
    }

    public String getAttribute(String attribute) {
        return Attributes.getAttribute(aClass, attribute);
    }

    public boolean hasAttribute(String attribute) {
        return Attributes.hasAttribute(aClass, attribute);
    }

    public String getAttribute(Field field, String attribute) {
        return Attributes.getAttribute(field, attribute);
    }

    public boolean hasAttribute(Field field, String attribute) {
        return Attributes.hasAttribute(field, attribute);
    }

    public String getAttribute(Method method, String attribute) {
        return Attributes.getAttribute(method, attribute);
    }

    public boolean hasAttribute(Method method, String attribute) {
        return Attributes.hasAttribute(method, attribute);
    }
}
