package com.tirsen.nanning.attribute;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

public class ClassAttributes {

    private static Map methodSignatureCache = new HashMap();

    private static final String CLASS_PREFIX = "class";
    private static final String FIELD_PREFIX = "field";
    private static final String METHOD_PREFIX = "method";

    private Class aClass;
    private Map classAttributes;
    private Map fieldAttributes;
    private Map methodAttributes;
    Properties properties;
    private String name;

    public ClassAttributes() {
        properties = new Properties();
    }

    public ClassAttributes(Class aClass) {
        this.aClass = aClass;
    }

    public void setClass(Class aClass) {
        this.aClass = aClass;
    }

    public String getName() {
        return aClass != null ? Attributes.className(aClass) : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private void maybeLoadAttributes() {
        if (classAttributes == null) {
            if (properties == null) {
                properties = Attributes.getProperties(aClass);
            }

            loadAttributes(properties);

            InputStream inputStream = null;
            try {
                // load the XML-defined attributes
                inputStream = Attributes.findFile(aClass, getName() + ".xml");
                if (inputStream == null) {
                    inputStream = Attributes.findFile(aClass, aClass.getName().replace('.', '/') + ".xml");
                }

                if (inputStream != null) {
                    AttributesXMLParser.parseXML(inputStream, this);
                }

            } catch (MalformedURLException e) {
                throw new AttributeException("Error fetching properties for " + aClass, e);
            } catch (IOException e) {
                throw new AttributeException("Error fetching properties for " + aClass, e);
            } catch (AttributeException e) {
                throw e;
            } catch (Exception e) {
                throw new AttributeException("Error fetching properties for " + aClass, e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        throw new AttributeException(e);
                    }
                }
            }
        }
        assert classAttributes != null : "properties not loaded";
        assert fieldAttributes != null : "properties not loaded";
        assert methodAttributes != null : "properties not loaded";
    }

    void loadAttributes(Properties properties) {
        classAttributes = new HashMap();
        fieldAttributes = new HashMap();
        methodAttributes = new HashMap();

        for (Iterator iterator = properties.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String propertyName = (String) entry.getKey();
            String attributeValue = (String) entry.getValue();

            String[] parts = propertyName.split("\\.");
            if (CLASS_PREFIX.equals(parts[0])) {
                String attributeName = joinTail(parts, 1);
                loadClassAttribute(attributeName, attributeValue);
            } else if (FIELD_PREFIX.equals(parts[0])) {
                String attributeName = joinTail(parts, 2);
                String fieldName = parts[1];
                loadFieldAttribute(fieldName, attributeName, attributeValue);
            } else if (METHOD_PREFIX.equals(parts[0])) {
                String attributeName = joinTail(parts, 2);
                String methodSignature = parts[1];
                loadMethodAttribute(methodSignature, attributeName, attributeValue);
            } else {
                throw new AttributeException("Invalid property " + propertyName);
            }
        }
    }

    void loadMethodAttribute(String methodSignature, String attributeName, String attributeValue) {
        assert methodSignature.indexOf('(') != -1 && methodSignature.indexOf(')') != -1:
                "method signature of wrong format: " + methodSignature;
        if (aClass != null) {
            if (methodAttributes == null) methodAttributes = new HashMap();

            Method method = findMethod(methodSignature);
            if (method != null) {
                getMap(methodAttributes, method).put(attributeName, attributeValue);
            } else {
                throw new AttributeException("Error while loading attributes, could not find method: " +
                                             methodSignature + " for class " + aClass.getName());
            }
        }
        if (properties == null) properties = new Properties();
        properties.put(METHOD_PREFIX + "." + methodSignature + "." + attributeName, attributeValue);
    }

    void loadFieldAttribute(String fieldName, String attributeName, String attributeValue) {
        if (aClass != null) {
            if (fieldAttributes == null) fieldAttributes = new HashMap();

            try {
                Field field = aClass.getDeclaredField(fieldName);
                getMap(fieldAttributes, field).put(attributeName, attributeValue);
            } catch (Exception e) {
                throw new AttributeException("Error while loading attributes, could not find field: " +
                                             fieldName + " for class " + aClass.getName(), e);
            }
        }
        if (properties == null) properties = new Properties();
        properties.put(FIELD_PREFIX + "." + fieldName + "." + attributeName, attributeValue);
    }

    void loadClassAttribute(String attributeName, String attributeValue) {
        if (aClass != null) {
            if (classAttributes == null) classAttributes = new HashMap();

            classAttributes.put(attributeName, attributeValue);
        }
        if (properties == null) properties = new Properties();
        properties.put(CLASS_PREFIX + "." + attributeName, attributeValue);
    }

    private Method findMethod(String methodSignature) {
        Method[] declaredMethods = aClass.getDeclaredMethods();
        for (int i = 0; i < declaredMethods.length; i++) {
            Method method = declaredMethods[i];
            if (methodSignature(method).equals(methodSignature)) {
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

    public static String joinTail(String[] parts, int firstIndex) {
        StringBuffer result = new StringBuffer();
        for (int i = firstIndex; i < parts.length; i++) {
            String part = parts[i];
            result.append(part);
            if (i < parts.length - 1) {
                result.append(".");
            }
        }
        return result.toString();
    }

    static String methodSignature(Method method) {
        String signature = (String) methodSignatureCache.get(method);
        if (signature == null) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(method.getName());
            stringBuffer.append('(');
            Class[] parameterTypes = method.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                Class parameterType = parameterTypes[i];
                String type = parameterType.getName();
                type = type.substring(type.lastIndexOf('.') + 1);
                if (type.lastIndexOf(';') != -1) {
                    type = type.substring(0, type.lastIndexOf(';'));
                }
                stringBuffer.append(type);
                if (i + 1 < parameterTypes.length) {
                    stringBuffer.append(',');
                }
            }
            stringBuffer.append(')');
            signature = stringBuffer.toString();
            methodSignatureCache.put(method, signature);
        }
        return signature;
    }

    public void store(OutputStream output, String name) throws IOException {
        properties.store(output, name);
    }
}
