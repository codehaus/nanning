/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.attribute;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * TODO document AttributesTest
 *
 * <!-- $Id: AttributesTest.java,v 1.8 2003-05-26 05:39:32 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.8 $
 */
public class AttributesTest extends AbstractAttributesTest {
    private Method method;
    private Field field;
    private Method argMethod;
    private Method arrayArgMethod;
    private Method overrideMethod;

    protected void setUp() throws Exception {
        super.setUp();
        
        method = AttributesTestClass.class.getMethod("method", null);
        overrideMethod = AttributesTestSubClass.class.getMethod("method", null);
        
        field = AttributesTestClass.class.getDeclaredField("field");
        argMethod = AttributesTestClass.class.getMethod("method", new Class[]{String.class, String.class});
        arrayArgMethod = AttributesTestClass.class.getMethod("method", new Class[]{String[].class});
    }

    public void testClassAttributes() throws IOException, NoSuchMethodException, NoSuchFieldException {
        ClassAttributes classAttributes = Attributes.getAttributes(AttributesTestClass.class);
        assertEquals("classValue", classAttributes.getAttribute("class.attribute"));
        assertTrue(classAttributes.hasAttribute("class.attribute"));
        assertFalse(classAttributes.hasAttribute("stupid.attribute"));

        assertEquals("fieldValue", classAttributes.getAttribute(field, "field.attribute"));
        assertTrue(classAttributes.hasAttribute(field, "field.attribute"));
        assertFalse(classAttributes.hasAttribute(field, "stupid.attribute"));

        assertEquals("methodValue", classAttributes.getAttribute(method, "method.attribute"));
        assertTrue(classAttributes.hasAttribute(method, "method.attribute"));
        assertFalse(classAttributes.hasAttribute(method, "stupid.attribute"));

        assertEquals("argMethodValue", classAttributes.getAttribute(argMethod, "method.attribute"));
        assertTrue(classAttributes.hasAttribute(argMethod, "method.attribute"));
        assertFalse(classAttributes.hasAttribute(argMethod, "stupid.attribute"));

        assertTrue(classAttributes.hasAttribute(arrayArgMethod, "method.attribute"));
        assertFalse(classAttributes.hasAttribute(arrayArgMethod, "stupid.attribute"));
        assertEquals("arrayArgMethodValue", classAttributes.getAttribute(arrayArgMethod, "method.attribute"));
    }

    public void testAttributes() throws IOException, NoSuchMethodException, NoSuchFieldException {
        // Test compiled source attributes...
        assertEquals("classValue", Attributes.getAttribute(AttributesTestClass.class, "class.attribute"));
        assertFalse(Attributes.hasAttribute(AttributesTestClass.class, "stupid.attribute"));
        Field field = AttributesTestClass.class.getDeclaredField("field");
        assertEquals("fieldValue", Attributes.getAttribute(field, "field.attribute"));
        assertFalse(Attributes.hasAttribute(field, "stupid.attribute"));
        Method method = AttributesTestClass.class.getMethod("method", null);
        assertEquals("methodValue", Attributes.getAttribute(method, "method.attribute"));
        assertFalse(Attributes.hasAttribute(method, "stupid.attribute"));
        Method argMethod = AttributesTestClass.class.getMethod("method", new Class[]{String.class, String.class});
        assertEquals("argMethodValue", Attributes.getAttribute(argMethod, "method.attribute"));
        assertFalse(Attributes.hasAttribute(argMethod, "stupid.attribute"));


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

    public void testJoinTail() {
        String[] parts = "field.field.field.attribute".split("\\.");
        assertEquals("field.attribute", ClassAttributes.joinTail(parts, 2));
        parts = "field.field.attribute".split("\\.");
        assertEquals("attribute", ClassAttributes.joinTail(parts, 2));
    }

    public void testHasInheritedAttribute() {
        assertTrue(Attributes.hasInheritedAttribute(method, "method.attribute"));
        assertFalse(Attributes.hasInheritedAttribute(method, "stupid.attribute"));

        assertTrue(Attributes.hasInheritedAttribute(overrideMethod, "method.attribute"));
        assertFalse(Attributes.hasInheritedAttribute(overrideMethod, "stupid.attribute"));

        assertTrue(Attributes.hasInheritedAttribute(overrideMethod, "interface.attribute"));
    }
}
