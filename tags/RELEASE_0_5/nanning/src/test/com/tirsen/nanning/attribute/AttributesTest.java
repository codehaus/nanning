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

import org.apache.commons.lang.StringUtils;

/**
 * TODO document AttributesTest
 *
 * <!-- $Id: AttributesTest.java,v 1.6 2003-04-14 17:33:00 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.6 $
 */
public class AttributesTest extends AbstractAttributesTest {

    public void testClassAttributes() throws IOException, NoSuchMethodException, NoSuchFieldException {
        ClassAttributes classAttributes = Attributes.getAttributes(AttributesTestClass.class);
        assertEquals("classValue", classAttributes.getAttribute("class.attribute"));
        assertTrue(classAttributes.hasAttribute("class.attribute"));
        assertFalse(classAttributes.hasAttribute("stupid.attribute"));

        Field field = AttributesTestClass.class.getDeclaredField("field");
        assertEquals("fieldValue", classAttributes.getAttribute(field, "field.attribute"));
        assertTrue(classAttributes.hasAttribute(field, "field.attribute"));
        assertFalse(classAttributes.hasAttribute(field, "stupid.attribute"));

        Method method = AttributesTestClass.class.getMethod("method", null);
        assertEquals("methodValue", classAttributes.getAttribute(method, "method.attribute"));
        assertTrue(classAttributes.hasAttribute(method, "method.attribute"));
        assertFalse(classAttributes.hasAttribute(method, "stupid.attribute"));

        Method argMethod = AttributesTestClass.class.getMethod("method", new Class[]{String.class, String.class});
        assertEquals("argMethodValue", classAttributes.getAttribute(argMethod, "method.attribute"));
        assertTrue(classAttributes.hasAttribute(argMethod, "method.attribute"));
        assertFalse(classAttributes.hasAttribute(argMethod, "stupid.attribute"));

        Method arrayArgMethod = AttributesTestClass.class.getMethod("method", new Class[]{String[].class});
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
        String[] parts = StringUtils.split("field.field.field.attribute", ".");
        assertEquals("field.attribute", ClassAttributes.joinTail(parts, 2));
        parts = StringUtils.split("field.field.attribute", ".");
        assertEquals("attribute", ClassAttributes.joinTail(parts, 2));
    }

}
