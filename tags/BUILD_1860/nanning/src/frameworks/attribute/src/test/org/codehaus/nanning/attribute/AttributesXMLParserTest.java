package org.codehaus.nanning.attribute;

/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

import junit.framework.TestCase;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * TODO document AttributesTagHandlerTest
 *
 * <!-- $Id: AttributesXMLParserTest.java,v 1.1 2003-07-04 10:53:57 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.1 $
 */
public class AttributesXMLParserTest extends TestCase {
    public void testAttributes() throws IOException, NoSuchMethodException, NoSuchFieldException, SAXException {
        ClassAttributes jobClassAttributes = new ClassAttributes(Job.class);
        new AttributesXMLParser().load(jobClassAttributes);

        //---- CHECK org.codehaus.nanning.Job ------------------

        //Check class attributes
        assertEquals("true", jobClassAttributes.getAttribute("persistant"));

        assertEquals("true", jobClassAttributes.getAttribute("secure"));

        //check field attributes
        Field descriptionField = Job.class.getDeclaredField("description");
        assertEquals("true", jobClassAttributes.getAttribute(descriptionField, "persistant"));

        Field bossField = Job.class.getDeclaredField("boss");
        assertEquals("true", jobClassAttributes.getAttribute(bossField, "bastard"));

        //check method attributes
        Method fireAllEmployeesMethod = Job.class.getDeclaredMethod("fireAllEmployees", null);
        Method hireEmployeeMethod = Job.class.getDeclaredMethod("hireEmployee", new Class[] {String.class, Employee.class});
        assertEquals("true", jobClassAttributes.getAttribute(fireAllEmployeesMethod, "secure"));
        assertEquals("false", jobClassAttributes.getAttribute(hireEmployeeMethod, "secure"));
    }

}