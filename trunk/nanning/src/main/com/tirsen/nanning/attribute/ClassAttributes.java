package com.tirsen.nanning.attribute;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Arrays;

public class ClassAttributes {
    private static final Log logger = LogFactory.getLog(ClassAttributes.class);

    private Class aClass;
    private Map classAttributes;
    private Map fieldAttributes;
    private Map methodAttributes;

    public ClassAttributes(Class aClass) {
        this.aClass = aClass;
    }

    private void maybeLoadAttributes() {
        if (classAttributes == null) {
            classAttributes = new HashMap();
            fieldAttributes = new HashMap();
            methodAttributes = new HashMap();
            for (Iterator iterator = Attributes.getProperties(aClass).entrySet().iterator(); iterator.hasNext();) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String propertyName = (String) entry.getKey();
                Object attributeValue = entry.getValue();
                String[] parts = StringUtils.split(propertyName, ".");
                if ("class".equals(parts[0])) {
                    classAttributes.put(joinTail(parts, 1), attributeValue);
                } else if ("field".equals(parts[0])) {
                    String fieldName = parts[1];
                    try {
                        Field field = aClass.getDeclaredField(fieldName);
                        getMap(fieldAttributes, field).put(joinTail(parts, 2), attributeValue);
                    } catch (Exception e) {
                        assert false : "Could not find field: " + fieldName;
                        logger.warn("Could not find field: " + fieldName, e);
                    }
                } else if ("method".equals(parts[0])) {
                    String methodSignature = parts[1];
                    Method method = findMethod(methodSignature);
                    if (method != null) {
                        getMap(methodAttributes, method).put(joinTail(parts, 2), attributeValue);
                    } else {
                        logger.warn("could not find method for " + methodSignature);
                    }
                } else {
                    assert false : "invalid property " + propertyName;
                }
            }
        }
        assert classAttributes != null : "properties not loaded";
        assert fieldAttributes != null : "properties not loaded";
        assert methodAttributes != null : "properties not loaded";
    }

    private Method findMethod(String methodSignature) {
        Method[] declaredMethods = aClass.getDeclaredMethods();
        for (int i = 0; i < declaredMethods.length; i++) {
            Method method = declaredMethods[i];
            if (Attributes.methodSignature(method).equals(methodSignature)) {
                return method;
            }
        }
        return null;
    }

    public String getAttribute(String attribute) {
        maybeLoadAttributes();
        return (String) classAttributes.get(attribute);
    }

    public boolean hasAttribute(String attribute) {
        maybeLoadAttributes();
        return classAttributes.containsKey(attribute);
    }

    public String getAttribute(Field field, String attribute) {
        maybeLoadAttributes();
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
        maybeLoadAttributes();
        return getMap(fieldAttributes, field).containsKey(attribute);
    }

    public String getAttribute(Method method, String attribute) {
        maybeLoadAttributes();
        return (String) getMap(methodAttributes, method).get(attribute);
    }

    public boolean hasAttribute(Method method, String attribute) {
        maybeLoadAttributes();
        return getMap(methodAttributes, method).containsKey(attribute);
    }

    public static Object joinTail(String[] parts, int firstIndex) {
        return StringUtils.join(Arrays.asList(parts).subList(firstIndex, parts.length).iterator(), ".");
    }
}
