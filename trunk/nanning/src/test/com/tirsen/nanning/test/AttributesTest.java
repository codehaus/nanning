/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.test;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.net.URL;

import com.tirsen.nanning.AttributesCompiler;
import com.tirsen.nanning.Attributes;

/**
 * TODO document AttributesTest
 *
 * <!-- $Id: AttributesTest.java,v 1.3 2002-10-30 21:39:28 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.3 $
 */
public class AttributesTest extends TestCase
{
    private File targetDir;
    private URL searchPath;

    protected void setUp() throws Exception
    {
        super.setUp();
        targetDir = File.createTempFile("attributes", ".tmp");
        targetDir.delete();
        targetDir.mkdirs();
        searchPath = targetDir.toURL();
        Attributes.addSearchPath(searchPath);
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
        Attributes.removeSearchPath(searchPath);
    }

    public void testAttributes() throws IOException, NoSuchMethodException, NoSuchFieldException
    {
        AttributesCompiler attributesCompiler = new AttributesCompiler();
        attributesCompiler.setSrc(new File("src" + File.separator + "test"));
        attributesCompiler.setDest(targetDir);
        attributesCompiler.execute();

        assertEquals("classValue", Attributes.getAttribute(AttributesTestClass.class, "classAttribute"));
        assertFalse(Attributes.hasAttribute(AttributesTestClass.class, "stupidAttribute"));
        Field field = AttributesTestClass.class.getDeclaredField("field");
        assertEquals("fieldValue", Attributes.getAttribute(field, "fieldAttribute"));
        assertFalse(Attributes.hasAttribute(field, "stupidAttribute"));
        Method method = AttributesTestClass.class.getMethod("method", null);
        assertEquals("methodValue", Attributes.getAttribute(method, "methodAttribute"));
        assertFalse(Attributes.hasAttribute(method, "stupidAttribute"));
        Method argMethod = AttributesTestClass.class.getMethod("method", new Class[]{ String.class, String.class });
        assertEquals("argMethodValue", Attributes.getAttribute(argMethod, "methodAttribute"));
        assertFalse(Attributes.hasAttribute(argMethod, "stupidAttribute"));
    }
}
