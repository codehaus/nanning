/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

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

 * <!-- $Id: Attributes.java,v 1.5 2002-11-24 12:29:09 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.5 $
 */
public class Attributes
{
    private static final Log logger = LogFactory.getLog(Attributes.class);

    private static List searchPaths = new ArrayList();
    private static Map propertiesCache = new HashMap();

    public static String getAttribute(Class klass, String attribute)
    {
        return getProperty(klass, attributeName(klass, attribute));
    }

    private static String attributeName(Class klass, String attribute)
    {
        return "class." + attribute;
    }

    private static String getProperty(Class klass, String key)
    {
        Properties properties = getProperties(klass);
        return properties.getProperty(key);
    }

    private static Properties getProperties(Class klass)
    {
        Properties properties = (Properties) propertiesCache.get(klass);
        if (properties == null) {
            InputStream inputStream = null;
            try
            {
                String className = klass.getName();

                String fileName = className.substring(className.lastIndexOf('.') + 1) + ".attributes";
                inputStream = klass.getResourceAsStream(fileName);

                fileName = className.replace('.', '/') + ".attributes";
                if (inputStream == null)
                {
                    inputStream = klass.getResourceAsStream('/' + fileName);
                }

                if (inputStream == null)
                {
                    for (Iterator iterator = searchPaths.iterator(); iterator.hasNext() && inputStream == null;)
                    {
                        URL searchPath = (URL) iterator.next();
                        URL url = new URL(searchPath, fileName);
                        try {
                            inputStream = url.openStream();
                        } catch (IOException ignore) {
                        }
                    }
                }

                if (inputStream != null)
                {
                    properties = new Properties();
                    properties.load(inputStream);
                    propertiesCache.put(klass, properties);
                }
                else
                {
                    throw new RuntimeException("Could not find attributes for " + klass);
                }
            }
            catch (MalformedURLException e)
            {
                throw new RuntimeException(e);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            finally
            {
                if (inputStream != null)
                {
                    try
                    {
                        inputStream.close();
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return properties;
    }

    public static String getAttribute(Method method, String attribute)
    {
        String name = attributeName(method, attribute);
        return getProperty(method.getDeclaringClass(), name);
    }

    private static String attributeName(Method method, String attribute)
    {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(method.getName());
        stringBuffer.append('(');
        Class[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++)
        {
            Class parameterType = parameterTypes[i];
            stringBuffer.append(parameterType.getName());
            if (i + 1 < parameterTypes.length)
            {
                stringBuffer.append(',');
            }
        }
        stringBuffer.append(')');
        stringBuffer.append('.');
        stringBuffer.append(attribute);
        String name = stringBuffer.toString();
        return name;
    }

    public static String getAttribute(Field field, String attribute)
    {
        return getProperty(field.getDeclaringClass(), attributeName(field, attribute));
    }

    private static String attributeName(Field field, String attribute)
    {
        return field.getName() + '.' + attribute;
    }

    public static void addSearchPath(URL searchPath)
    {
        searchPaths.add(searchPath);
    }

    public static void removeSearchPath(URL searchPath)
    {
        searchPaths.add(searchPath);
    }

    public static boolean hasAttribute(Class klass, String attribute)
    {
        return getProperties(klass).containsKey(attributeName(klass, attribute));
    }

    public static boolean hasAttribute(Method method, String attribute)
    {
        return getProperties(method.getDeclaringClass()).containsKey(attributeName(method, attribute));
    }

    public static boolean hasAttribute(Field field, String attribute)
    {
        return getProperties(field.getDeclaringClass()).containsKey(attributeName(field, attribute));
    }
}
