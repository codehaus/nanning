/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.attribute;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.*;
import java.io.InputStream;
import java.io.IOException;

/**
 * TODO document Attributes
 *
 * TODO: there's actually a nasty little bug in here
 * If there are two methods with the same name, same set of arguments with types with same name but
 * in _different_ packages the attributes with same name of these methods will collide.

 * <!-- $Id: Attributes.java,v 1.14 2003-06-10 05:26:46 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.14 $
 */

public class Attributes {
    private static List searchPaths = new ArrayList();
    private static Map classAttributesCache = new HashMap();

    public static String getAttribute(Class klass, String attribute) {
        return getAttributes(klass).getAttribute(attribute);
    }

    public static String getAttribute(Method method, String attribute) {
        return getAttributes(method.getDeclaringClass()).getAttribute(method, attribute);
    }

    public static String getAttribute(Field field, String attribute) {
        return getAttributes(field.getDeclaringClass()).getAttribute(field, attribute);
    }

    public static void addSearchPath(URL searchPath) {
        searchPaths.add(searchPath);
    }

    public static void removeSearchPath(URL searchPath) {
        searchPaths.add(searchPath);
    }

    public static boolean hasAttribute(Class klass, String attribute) {
        return getAttributes(klass).hasAttribute(attribute);
    }

    public static boolean hasAttribute(Method method, String attribute) {
        return hasAttribute(method.getDeclaringClass(), method, attribute);
    }

    private static boolean hasAttribute(Class aClass, Method method, String attribute) {
        return getAttributes(aClass).hasAttribute(method, attribute);
    }

    public static boolean hasAttribute(Field field, String attribute) {
        return hasAttribute(field.getDeclaringClass(), field, attribute);
    }

    private static boolean hasAttribute(Class aClass, Field field, String attribute) {
        return getAttributes(aClass).hasAttribute(field, attribute);
    }

    public static boolean hasInheritedAttribute(Field field, String attribute) {
        return hasInheritedAttribute(field.getDeclaringClass(), field, attribute);
    }

    public static boolean hasInheritedAttribute(Class aClass, Field field, String attribute) {
        if (aClass == null) {
            return false;
        }

        if (hasAttribute(aClass, field, attribute)) {
            return true;
        } else {
            if (hasInheritedAttribute(aClass.getSuperclass(), field, attribute)) {
                return true;
            }
            Class[] interfaces = aClass.getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                Class anInterface = interfaces[i];
                if (hasInheritedAttribute(anInterface, field, attribute)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static boolean hasInheritedAttribute(Method method, String attribute) {
        return hasInheritedAttribute(method.getDeclaringClass(), method, attribute);
    }

    public static boolean hasInheritedAttribute(Class aClass, Method method, String attribute) {
        if (aClass == null) {
            return false;
        }

        try {
            method = aClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
        } catch (NoSuchMethodException e) {
        }

        if (hasAttribute(aClass, method, attribute)) {
            return true;
        } else {
            Class[] interfaces = aClass.getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                Class anInterface = interfaces[i];
                if (hasInheritedAttribute(anInterface, method, attribute)) {
                    return true;
                }
            }

            Class superclass = aClass.getSuperclass();
            if (hasInheritedAttribute(superclass, method, attribute)) {
                return true;
            }
            return false;
        }
    }

    public static String getInheritedAttribute(Class aClass, String attribute) {
        if (aClass == null) {
            return null;
        }

        if (hasAttribute(aClass, attribute)) {
            return getAttribute(aClass, attribute);
        } else {
            String attributeValue = getInheritedAttribute(aClass.getSuperclass(), attribute);
            if (attributeValue == null) {
                Class[] interfaces = aClass.getInterfaces();
                for (int i = 0; i < interfaces.length; i++) {
                    Class anInterface = interfaces[i];
                    attributeValue = getInheritedAttribute(anInterface, attribute);
                    if (attributeValue != null) {
                        break;
                    }
                }
            }
            return attributeValue;
        }
    }

    public static boolean hasInheritedAttribute(Class aClass, String attribute) {
        if (aClass == null) {
            return false;
        }

        if (hasAttribute(aClass, attribute)) {
            return true;
        } else {
            if (hasInheritedAttribute(aClass.getSuperclass(), attribute)) {
                return true;
            }
            Class[] interfaces = aClass.getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                Class anInterface = interfaces[i];
                if (hasInheritedAttribute(anInterface, attribute)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static ClassAttributes getAttributes(Class aClass) {
        ClassAttributes classAttributes = (ClassAttributes) classAttributesCache.get(aClass);
        if (classAttributes == null) {
            classAttributes = new ClassAttributes(aClass);
            new PropertyFileAttributeLoader().load(classAttributes);
            new AttributesXMLParser().load(classAttributes);
            classAttributesCache.put(aClass, classAttributes);
        }
        return classAttributes;
    }

    public static URL[] getSearchPath() {
        return (URL[]) searchPaths.toArray(new URL[0]);
    }

    static InputStream findFile(Class klass, String fileName) throws MalformedURLException {
        InputStream inputStream = klass.getResourceAsStream(fileName);

        if (inputStream == null) {
            inputStream = klass.getResourceAsStream('/' + fileName);
        }

        if (inputStream == null) {
            for (Iterator iterator = searchPaths.iterator(); iterator.hasNext();) {
                URL searchPath = (URL) iterator.next();
                URL url = new URL(searchPath, fileName);
                try {
                    inputStream = url.openStream();
                } catch (IOException ignore) {
                }
            }
        }
        return inputStream;
    }
}