/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.attribute;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO document Attributes
 *
 * TODO: there's actually a nasty little bug in here
 * If there are two methods with the same name, same set of arguments with types with same name but
 * in _different_ packages the attributes with same name of these methods will collide.
 * To resolve this I need to parse the imports and check if they contain classes, setup classpaths and so on and so
 * forth. At this moment I don't feel up to that... :-) There may be a smart solution to it, but I just don't see it.
 * Hmm... wait, a minute, there's some support for this in QDox, maybe that will work...
 * -- jon

 * <!-- $Id: Attributes.java,v 1.8 2003-03-21 17:11:10 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.8 $
 */

public class Attributes {
    private static final Log logger = LogFactory.getLog(Attributes.class);

    private static List searchPaths = new ArrayList();
    private static Map propertiesCache = new HashMap();
    private static Map methodPropertyNameCache = new HashMap();
    private static Map classAttributesCache = new HashMap();

    public static String getAttribute(Class klass, String attribute) {
        return getAttributes(klass).getAttribute(attribute);
    }

    static Properties getProperties(Class klass) {
        Properties properties = (Properties) propertiesCache.get(klass);
        if (properties == null) {
            InputStream inputStream = null;
            try {
                properties = (Properties) propertiesCache.get(klass);
                if (properties == null) {
                    properties = new Properties();
                    propertiesCache.put(klass, properties);
                }
                assert properties != null : "no properties created for " + klass;

                String className = klass.getName();

                boolean found = false;

                // load the JavaDoc-tags
                inputStream = findFile(klass, className.substring(className.lastIndexOf('.') + 1) + ".attributes");
                if (inputStream == null) {
                    inputStream = findFile(klass, className.replace('.', '/') + ".attributes");
                }

                if (inputStream != null) {
                    properties.load(inputStream);
                    found = true;
                    inputStream.close();
                }

                inputStream = null;

                // load the XML-defined attributes
                inputStream = findFile(klass, className.substring(className.lastIndexOf('.') + 1) + ".xml");
                if (inputStream == null) {
                    inputStream = findFile(klass, className.replace('.', '/') + ".xml");
                }

                if (inputStream != null) {
                    properties.putAll(AttributesXMLParser.parseXML(inputStream));
                    found = true;
                }

                if (!found) {
                    logger.debug("could not find attributes for " + klass + " on classpath or in " + searchPaths);
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException("Error fetching properties for " + klass, e);
            } catch (IOException e) {
                throw new RuntimeException("Error fetching properties for " + klass, e);
            } catch (Exception e) {
                throw new RuntimeException("Error fetching properties for " + klass, e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return properties;
    }

    private static InputStream findFile(Class klass, String fileName) throws MalformedURLException {
        InputStream inputStream = klass.getResourceAsStream(fileName);

        if (inputStream == null) {
            inputStream = klass.getResourceAsStream('/' + fileName);
        }

        if (inputStream == null) {
            for (Iterator iterator = searchPaths.iterator(); iterator.hasNext() && inputStream == null;) {
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

    public static String getAttribute(Method method, String attribute) {
        return getAttributes(method.getDeclaringClass()).getAttribute(method, attribute);
    }

    static String methodSignature(Method method) {
        String signature = (String) methodPropertyNameCache.get(method);
        if (signature == null) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(method.getName());
            stringBuffer.append('(');
            Class[] parameterTypes = method.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                Class parameterType = parameterTypes[i];
                String type = parameterType.getName();
                stringBuffer.append(type.substring(type.lastIndexOf('.') + 1));
                if (i + 1 < parameterTypes.length) {
                    stringBuffer.append(',');
                }
            }
            stringBuffer.append(')');
            signature = stringBuffer.toString();
            methodPropertyNameCache.put(method, signature);
        }
        return signature;
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
            classAttributesCache.put(aClass, classAttributes);
        }
        return classAttributes;
    }
}