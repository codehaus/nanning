/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.attribute;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.MalformedURLException;

import com.tirsen.nanning.attribute.AttributesCompiler;
import com.tirsen.nanning.attribute.Attributes;
import com.tirsen.nanning.attribute.AttributesTestClass;

/**
 * TODO document AttributesTest
 *
 * <!-- $Id: AttributesTest.java,v 1.2 2003-01-19 12:09:04 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
 */
public class AttributesTest extends TestCase {
    private File targetDir;
    private URL searchPath;
    private static boolean attributesCompiled = false;

    protected void setUp() throws Exception {
        super.setUp();
        targetDir = File.createTempFile("attributes", ".tmp");
        targetDir.delete();
        targetDir.mkdirs();
        searchPath = targetDir.toURL();
        Attributes.addSearchPath(searchPath);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        Attributes.removeSearchPath(searchPath);
    }

    public void testClassAttributes() throws IOException, NoSuchMethodException, NoSuchFieldException {
        ClassAttributes classAttributes = Attributes.getAttributes(AttributesTestClass.class);
        assertEquals("classValue", classAttributes.getAttribute("classAttribute"));
        assertTrue(classAttributes.hasAttribute("classAttribute"));
        assertFalse(classAttributes.hasAttribute("stupidAttribute"));
        Field field = AttributesTestClass.class.getDeclaredField("field");
        assertEquals("fieldValue", classAttributes.getAttribute(field, "fieldAttribute"));
        assertTrue(classAttributes.hasAttribute(field, "fieldAttribute"));
        assertFalse(classAttributes.hasAttribute(field, "stupidAttribute"));
        Method method = AttributesTestClass.class.getMethod("method", null);
        assertEquals("methodValue", classAttributes.getAttribute(method, "methodAttribute"));
        assertTrue(classAttributes.hasAttribute(method, "methodAttribute"));
        assertFalse(classAttributes.hasAttribute(method, "stupidAttribute"));
        Method argMethod = AttributesTestClass.class.getMethod("method", new Class[]{String.class, String.class});
        assertEquals("argMethodValue", classAttributes.getAttribute(argMethod, "methodAttribute"));
        assertTrue(classAttributes.hasAttribute(argMethod, "methodAttribute"));
        assertFalse(classAttributes.hasAttribute(argMethod, "stupidAttribute"));
    }

    public void testAttributes() throws IOException, NoSuchMethodException, NoSuchFieldException {
        AttributesCompiler attributesCompiler = new AttributesCompiler();
        attributesCompiler.setSrc(new File("src" + File.separator + "test"));
        attributesCompiler.setDest(targetDir);
        attributesCompiler.execute();

        // Test compiled source attributes...
        assertEquals("classValue", Attributes.getAttribute(AttributesTestClass.class, "classAttribute"));
        assertFalse(Attributes.hasAttribute(AttributesTestClass.class, "stupidAttribute"));
        Field field = AttributesTestClass.class.getDeclaredField("field");
        assertEquals("fieldValue", Attributes.getAttribute(field, "fieldAttribute"));
        assertFalse(Attributes.hasAttribute(field, "stupidAttribute"));
        Method method = AttributesTestClass.class.getMethod("method", null);
        assertEquals("methodValue", Attributes.getAttribute(method, "methodAttribute"));
        assertFalse(Attributes.hasAttribute(method, "stupidAttribute"));
        Method argMethod = AttributesTestClass.class.getMethod("method", new Class[]{String.class, String.class});
        assertEquals("argMethodValue", Attributes.getAttribute(argMethod, "methodAttribute"));
        assertFalse(Attributes.hasAttribute(argMethod, "stupidAttribute"));


        // Test xml attributes
        //School
        assertEquals("classValue", Attributes.getAttribute(School.class, "classAttrib"));
        assertFalse(Attributes.hasAttribute(School.class, "stupidAttribute"));
        Field xmlfield = School.class.getDeclaredField("name");
        assertEquals("fieldValue", Attributes.getAttribute(xmlfield, "fieldAttrib"));
        assertFalse(Attributes.hasAttribute(xmlfield, "stupidAttribute"));
        Method xmlMethod = School.class.getMethod("sackAllTeachers", null);
        assertEquals("false", Attributes.getAttribute(xmlMethod, "secure"));
        assertFalse(Attributes.hasAttribute(xmlMethod, "stupidAttribute"));
        Method xmlArgMethod = School.class.getMethod("setName", new Class[]{String.class});
        assertEquals("methodValue", Attributes.getAttribute(xmlArgMethod, "methodAttrib"));
        assertFalse(Attributes.hasAttribute(xmlArgMethod, "stupidAttribute"));

        // Job - Shows mixed attributes
        assertEquals("true", Attributes.getAttribute(Job.class, "persistant"));
        assertEquals("required", Attributes.getAttribute(Job.class, "transaction"));
        assertEquals("true", Attributes.getAttribute(Job.class, "secure"));

        Field descfield = Job.class.getDeclaredField("description");
        assertEquals("true", Attributes.getAttribute(descfield, "persistant"));
        assertEquals("true", Attributes.getAttribute(descfield, "transient"));
        assertFalse(Attributes.hasAttribute(descfield, "transaction"));

        Method hireMethod = Job.class.getMethod("hireEmployee", new Class[]{String.class, Employee.class});
        assertEquals("great", Attributes.getAttribute(hireMethod, "nanning"));
        assertEquals("false", Attributes.getAttribute(hireMethod, "secure"));
        assertFalse(Attributes.hasAttribute(hireMethod, "transient"));
        assertTrue(Attributes.hasAttribute(hireMethod, "nanning"));

        Method fireMethod = Job.class.getMethod("fireAllEmployees", null);
        assertEquals("true", Attributes.getAttribute(fireMethod, "secure"));
    }

    public static void compileAttributes() {
        if (!attributesCompiled) {
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
