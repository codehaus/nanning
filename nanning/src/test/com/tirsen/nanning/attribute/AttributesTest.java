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
 * <!-- $Id: AttributesTest.java,v 1.9 2003-06-09 17:40:42 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.9 $
 */
public class AttributesTest extends AbstractAttributesTest {
    public static final String CLASS_ATTRIBUTE = "class.attribute";
    public static final String FIELD_ATTRIBUTE = "field.attribute";
    public static final String FIELD_VALUE = "fieldValue";
    public static final String METHOD_ATTRIBUTE = "method.attribute";

    public static final Method method;
    public static final Field field;
    public static final Method argMethod;
    public static final Method arrayArgMethod;
    public static final Method overridingMethod;
    public static final String METHOD_VALUE = "methodValue";

    static {
        try {
            method = AttributesTestClass.class.getMethod("method", null);
            overridingMethod = AttributesTestSubClass.class.getMethod("method", null);

            field = AttributesTestClass.class.getDeclaredField("field");
            argMethod = AttributesTestClass.class.getMethod("method", new Class[]{String.class, String.class});
            arrayArgMethod = AttributesTestClass.class.getMethod("method", new Class[]{String[].class});
        } catch (Exception e) {
            throw new Error("Could not reflect AttributesTestClass", e);
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testClassAttributes() throws IOException, NoSuchMethodException, NoSuchFieldException {
        ClassAttributes classAttributes = Attributes.getAttributes(AttributesTestClass.class);
        assertEquals("classValue", classAttributes.getAttribute(CLASS_ATTRIBUTE));
        assertTrue(classAttributes.hasAttribute("class.attribute"));
        assertFalse(classAttributes.hasAttribute("stupid.attribute"));

        assertEquals(FIELD_VALUE, classAttributes.getAttribute(field, FIELD_ATTRIBUTE));
        assertTrue(classAttributes.hasAttribute(field, "field.attribute"));
        assertFalse(classAttributes.hasAttribute(field, "stupid.attribute"));

        assertEquals(METHOD_VALUE, classAttributes.getAttribute(method, METHOD_ATTRIBUTE));
        assertTrue(classAttributes.hasAttribute(method, METHOD_ATTRIBUTE));
        assertFalse(classAttributes.hasAttribute(method, "stupid.attribute"));

        assertEquals("argMethodValue", classAttributes.getAttribute(argMethod, METHOD_ATTRIBUTE));
        assertTrue(classAttributes.hasAttribute(argMethod, METHOD_ATTRIBUTE));
        assertFalse(classAttributes.hasAttribute(argMethod, "stupid.attribute"));

        assertTrue(classAttributes.hasAttribute(arrayArgMethod, METHOD_ATTRIBUTE));
        assertFalse(classAttributes.hasAttribute(arrayArgMethod, "stupid.attribute"));
        assertEquals("arrayArgMethodValue", classAttributes.getAttribute(arrayArgMethod, METHOD_ATTRIBUTE));
    }

    public void testAttributes() throws IOException, NoSuchMethodException, NoSuchFieldException {
        // Test compiled source attributes...
        assertEquals("classValue", Attributes.getAttribute(AttributesTestClass.class, CLASS_ATTRIBUTE));
        assertFalse(Attributes.hasAttribute(AttributesTestClass.class, "stupid.attribute"));
        Field field = AttributesTestClass.class.getDeclaredField("field");
        assertEquals(FIELD_VALUE, Attributes.getAttribute(field, FIELD_ATTRIBUTE));
        assertFalse(Attributes.hasAttribute(field, "stupid.attribute"));
        Method method = AttributesTestClass.class.getMethod("method", null);
        assertEquals(METHOD_VALUE, Attributes.getAttribute(method, METHOD_ATTRIBUTE));
        assertFalse(Attributes.hasAttribute(method, "stupid.attribute"));
        Method argMethod = AttributesTestClass.class.getMethod("method", new Class[]{String.class, String.class});
        assertEquals("argMethodValue", Attributes.getAttribute(argMethod, METHOD_ATTRIBUTE));
        assertFalse(Attributes.hasAttribute(argMethod, "stupid.attribute"));


        // Test xml attributes
        //School
        assertEquals("classValue", Attributes.getAttribute(School.class, "classAttrib"));
        assertFalse(Attributes.hasAttribute(School.class, "stupidAttribute"));
        Field xmlfield = School.class.getDeclaredField("name");
        assertEquals(FIELD_VALUE, Attributes.getAttribute(xmlfield, "fieldAttrib"));
        assertFalse(Attributes.hasAttribute(xmlfield, "stupidAttribute"));
        Method xmlMethod = School.class.getMethod("sackAllTeachers", null);
        assertEquals("false", Attributes.getAttribute(xmlMethod, "secure"));
        assertFalse(Attributes.hasAttribute(xmlMethod, "stupidAttribute"));
        Method xmlArgMethod = School.class.getMethod("setName", new Class[]{String.class});
        assertEquals(METHOD_VALUE, Attributes.getAttribute(xmlArgMethod, "methodAttrib"));
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
        assertTrue(Attributes.hasInheritedAttribute(method, METHOD_ATTRIBUTE));
        assertFalse(Attributes.hasInheritedAttribute(method, "stupid.attribute"));

        assertTrue(Attributes.hasInheritedAttribute(overridingMethod, METHOD_ATTRIBUTE));
        assertFalse(Attributes.hasInheritedAttribute(overridingMethod, "stupid.attribute"));

        assertTrue(Attributes.hasInheritedAttribute(overridingMethod, "interface.attribute"));
    }
}
