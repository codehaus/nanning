/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.MalformedURLException;

import com.tirsen.nanning.AttributesCompiler;
import com.tirsen.nanning.Attributes;

/**
 * TODO document AttributesTest
 *
 * <!-- $Id: AttributesTest.java,v 1.3 2002-12-04 07:45:33 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.3 $
 */
public class AttributesTest extends TestCase
{
    private File targetDir;
    private URL searchPath;
    private static boolean attributesCompiled = false;

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

    public static void compileAttributes() {
        if (attributesCompiled) {
            attributesCompiled = true;
            File targetDir = new File("target" + File.separator + "attributes");
            try {
                Attributes.addSearchPath(targetDir.toURL());
            } catch (MalformedURLException e) {
                fail(e.getMessage());
            }
            AttributesCompiler attributesCompiler = new AttributesCompiler();
            attributesCompiler.setSrc(new File("src" + File.separator + "test"));
            attributesCompiler.setDest(targetDir);
            attributesCompiler.execute();
            attributesCompiler.setSrc(new File("src" + File.separator + "main"));
            attributesCompiler.execute();
        }
    }
}
