package com.tirsen.nanning.attribute;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ClassAttributes {

    private Class attributeClass;
    private Map classAttributes = new HashMap();
    private Map fieldAttributes = new HashMap();
    private Map methodAttributes = new HashMap();

    public ClassAttributes(Class aClass) {
        this.attributeClass = aClass;
    }

    public Class getAttributeClass() {
        return attributeClass;
    }

    void setMethodAttribute(Method method, String attributeName, String attributeValue) {
        getMap(methodAttributes, method).put(attributeName, attributeValue);
    }

    void setFieldAttribute(Field field, String attributeName, String attributeValue) {
        getMap(fieldAttributes, field).put(attributeName, attributeValue);
    }

    void setClassAttribute(String attributeName, String attributeValue) {
        classAttributes.put(attributeName, attributeValue);
    }

    public String getAttribute(String attribute) {
        return (String) classAttributes.get(attribute);
    }

    public boolean hasAttribute(String attribute) {
        return classAttributes.containsKey(attribute);
    }

    public String getAttribute(Field field, String attribute) {
        return (String) getMap(fieldAttributes, field).get(attribute);
    }

    private Map getMap(Map map, Object key) {
        assert map != null : "properties not loaded";
        Map result = (Map) map.get(key);
        if (result == null) {
            result = new HashMap();
            map.put(key, result);
        }
        return result;
    }

    public boolean hasAttribute(Field field, String attribute) {
        return getMap(fieldAttributes, field).containsKey(attribute);
    }

    public String getAttribute(Method method, String attribute) {
        return (String) getMap(methodAttributes, method).get(attribute);
    }

    public boolean hasAttribute(Method method, String attribute) {
        return getMap(methodAttributes, method).containsKey(attribute);
    }
}
