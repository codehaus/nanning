package org.codehaus.nanning.attribute;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import org.codehaus.nanning.util.OroUtils;
import org.codehaus.nanning.AssertionException;

public class ClassPropertiesHelper {

    private static Map methodSignatureCache = new HashMap();

    private static final String CLASS_PREFIX = "class";
    private static final String FIELD_PREFIX = "field";
    private static final String METHOD_PREFIX = "method";

    Properties properties;
    private String className;
    private String packageName;
    private ClassAttributes targetClassAttributes;
    private Class attributeClass;

    public ClassPropertiesHelper() {
        properties = new Properties();
    }

    public ClassPropertiesHelper(ClassAttributes classAttributes) {
        this();
        setTargetClassAttributes(classAttributes);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setTargetClassAttributes(ClassAttributes classAttributes) {
        targetClassAttributes = classAttributes;
        setAttributeClass(classAttributes.getAttributeClass());
    }

    private void setAttributeClass(Class attributeClass) {
        this.attributeClass = attributeClass;
        if (attributeClass != null) {
            className = className(attributeClass);
            packageName = packageName(attributeClass);
        }
    }

    void loadAttributes(Properties properties) {
        if (targetClassAttributes == null) {
            throw new AssertionException();
        }

        this.properties = properties;

        for (Iterator iterator = properties.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String propertyName = (String) entry.getKey();
            String attributeValue = (String) entry.getValue();

            String[] parts = OroUtils.split(propertyName, "\\.");
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
        if (methodSignature.indexOf('(') == -1 || methodSignature.indexOf(')') == -1) {
            throw new AssertionException("method signature of wrong format: " + methodSignature);
        }
        if (targetClassAttributes != null) {
            Method method = findMethod(methodSignature);
            if (method == null) {
                throw new AttributeException("Error while loading attributes, could not find method " +
                                             methodSignature + " for " + attributeClass);
            }

            targetClassAttributes.setMethodAttribute(method, attributeName, attributeValue);
        }
        properties.put(METHOD_PREFIX + "." + methodSignature + "." + attributeName, attributeValue);
    }

    void loadFieldAttribute(String fieldName, String attributeName, String attributeValue) {
        if (targetClassAttributes != null) {
            try {
                Field field = attributeClass.getDeclaredField(fieldName);
                targetClassAttributes.setFieldAttribute(field, attributeName, attributeValue);
            } catch (Exception e) {
                throw new AttributeException("Error while loading attributes, could not find field: " +
                                             fieldName + " for " + attributeClass, e);
            }
        }
        properties.put(FIELD_PREFIX + "." + fieldName + "." + attributeName, attributeValue);
    }

    void loadClassAttribute(String attributeName, String attributeValue) {
        if (targetClassAttributes != null) {
            if (attributeClass != null) {
                targetClassAttributes.setClassAttribute(attributeName, attributeValue);
            }
        }
        properties.put(CLASS_PREFIX + "." + attributeName, attributeValue);
    }

    private Method findMethod(String methodSignature) {
        Method[] declaredMethods = attributeClass.getDeclaredMethods();
        for (int i = 0; i < declaredMethods.length; i++) {
            Method method = declaredMethods[i];
            if (methodSignature(method).equals(methodSignature)) {
                return method;
            }
        }
        return null;
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
                if (parameterType.isArray()) {
                    parameterType = parameterType.getComponentType();
                }
                String type = parameterType.getName();
                type = type.substring(type.lastIndexOf('.') + 1);
                type = type.substring(type.lastIndexOf('$') + 1);
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

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    static String className(Class klass) {
        String className = klass.getName();
        return className.substring(className.lastIndexOf('.') + 1);
    }

    static String packageName(Class klass) {
        String className = klass.getName();
        int lastDot = className.lastIndexOf('.');
        if (lastDot == -1) {
            return "";
        }
        return className.substring(0, lastDot);
    }

    public void transferAttributesToTarget() {
        loadAttributes(properties);
    }

    public File getAttributeFile(File baseDir) {
        return new File(baseDir, getPackageName().replace('.', File.separatorChar) + File.separator + getClassName() +
                                 PropertyFileAttributeLoader.ATTRIBUTE_FILE_SUFFIX);
    }

    public void store(File dest) throws IOException {
        OutputStream output = new FileOutputStream(getAttributeFile(dest));
        try {
            properties.store(output, getPackageName() + "." + getClassName());
        } finally {
            output.close();
        }
    }
}
