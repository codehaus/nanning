/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

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

 * <!-- $Id: Attributes.java,v 1.1 2002-10-28 21:45:34 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public class Attributes
{
    private static List searchPaths = new ArrayList();

    public static String getAttribute(Class klass, String attribute)
    {
        String key = "class." + attribute;
        return getProperty(klass, key);
    }

    private static String getProperty(Class klass, String key)
    {
        Properties properties;
        String fileName = klass.getName().replace('.', '/') + ".attributes";
        InputStream inputStream = null;
        try
        {
            inputStream = klass.getResourceAsStream('/' + fileName);
            if(inputStream == null)
            {
                for (Iterator iterator = searchPaths.iterator(); iterator.hasNext();)
                {
                    URL searchPath = (URL) iterator.next();
                    URL url = new URL(searchPath, fileName);
                    inputStream = url.openStream();
                }
            }
            if(inputStream != null)
            {
                properties = new Properties();
                properties.load(inputStream);
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
            if(inputStream != null)
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
        return properties.getProperty(key);
    }

    public static String getAttribute(Method method, String attribute)
    {
        StringBuffer name = new StringBuffer();
        name.append(method.getName());
        name.append('(');
        Class[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++)
        {
            Class parameterType = parameterTypes[i];
            name.append(parameterType.getName());
            if(i + 1 < parameterTypes.length)
            {
                name.append(',');
            }
        }
        name.append(')');
        name.append('.');
        name.append(attribute);
        return getProperty(method.getDeclaringClass(), name.toString());
    }

    public static String getAttribute(Field field, String attribute)
    {
        return getProperty(field.getDeclaringClass(), field.getName() + '.' + attribute);
    }

    public static void addSearchPath(URL searchPath)
    {
        searchPaths.add(searchPath);
    }

    public static void removeSearchPath(URL searchPath)
    {
        searchPaths.add(searchPath);
    }
}
