/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.attribute;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

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

 * <!-- $Id: Attributes.java,v 1.4 2003-01-19 12:09:04 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.4 $
 */

public class Attributes {
    private static final Log logger = LogFactory.getLog(Attributes.class);

    private static List searchPaths = new ArrayList();
    private static Map propertiesCache = new HashMap();
    private static Map methodPropertyNameCache = new HashMap();
    private static Map xmlAttributes = new HashMap();

    public static String getAttribute(Class klass, String attribute) {
        return getProperty(klass, propertyName(klass, attribute));
    }

    private static String propertyName(Class klass, String attribute) {
        return "class." + attribute;
    }

    private static String getProperty(Class klass, String key) {
        Properties properties = getProperties(klass);
        if (properties != null) {
            String value = properties.getProperty(key);
            return value;
        } else {
            return null;
        }
    }

    private static Properties getProperties(Class klass) {
        Properties properties = (Properties) propertiesCache.get(klass);
        if (properties == null) {
            InputStream inputStream = null;
            try {
                String className = klass.getName();

                // load the JavaDoc-tags
                inputStream = findFile(klass, className.substring(className.lastIndexOf('.') + 1) + ".attributes");
                if (inputStream == null) {
                    inputStream = findFile(klass, className.replace('.', '/') + ".attributes");
                }

                if (inputStream != null) {
                    properties = maybeCreateProperties(klass);

                    properties.load(inputStream);
                }

                if (inputStream != null) {
                    inputStream.close();
                }
                inputStream = null;

                // load the XML-defined attributes
                inputStream = findFile(klass, className.substring(className.lastIndexOf('.') + 1) + ".xml");
                if (inputStream == null) {
                    inputStream = findFile(klass, className.replace('.', '/') + ".xml");
                }

                if (inputStream != null) {
                    properties = maybeCreateProperties(klass);
                    properties.putAll(AttributesXMLParser.parseXML(inputStream));
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

    private static Properties maybeCreateProperties(Class klass) {
        Properties properties = (Properties) propertiesCache.get(klass);
        if (properties == null) {
            properties = new Properties();
            propertiesCache.put(klass, properties);
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
        String name = propertyName(method, attribute);
        return getProperty(method.getDeclaringClass(), name);
    }

    private static String propertyName(Method method, String attribute) {
        String propertyName = (String) methodPropertyNameCache.get(method);
        if (propertyName == null) {
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
            propertyName = stringBuffer.toString();
            methodPropertyNameCache.put(method, propertyName);
        }
        return propertyName + "." + attribute;
    }

    public static String getAttribute(Field field, String attribute) {
        return getProperty(field.getDeclaringClass(), propertyName(field, attribute));
    }

    private static String propertyName(Field field, String attribute) {
        return field.getName() + '.' + attribute;
    }

    public static void addSearchPath(URL searchPath) {
        searchPaths.add(searchPath);
    }

    public static void removeSearchPath(URL searchPath) {
        searchPaths.add(searchPath);
    }

    public static boolean hasAttribute(Class klass, String attribute) {
        Properties properties = getProperties(klass);
        if (properties == null) {
            return false;
        }
        return properties.containsKey(propertyName(klass, attribute));
    }

    public static boolean hasAttribute
            (Method
            method, String
            attribute) {
        Properties properties = getProperties(method.getDeclaringClass());
        if (properties == null) {
            return false;
        }
        return properties.containsKey(propertyName(method, attribute));
    }

    public static boolean hasAttribute(Field field, String attribute) {
        Properties properties = getProperties(field.getDeclaringClass());
        if (properties == null) {
            return false;
        }
        return properties.containsKey(propertyName(field, attribute));
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
                if(hasInheritedAttribute(anInterface, attribute)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static ClassAttributes getAttributes(Class aClass) {
        return new ClassAttributes(aClass);
    }
}