package com.tirsen.nanning.attribute;

/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import junit.framework.TestCase;
import org.xml.sax.SAXException;

/**
 * TODO document AttributesTagHandlerTest
 *
 * <!-- $Id: AttributesXMLParserTest.java,v 1.3 2003-03-21 17:11:14 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.3 $
 */
public class AttributesXMLParserTest extends TestCase {
    public void testAttributes() throws IOException, NoSuchMethodException, NoSuchFieldException, SAXException {
        String resource = Job.class.getName().replace('.', '/') + ".xml";
        InputStream is =
                Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);

        Properties jobClassAttributes = AttributesXMLParser.parseXML(is);

        //---- CHECK com.tirsen.nanning.Job ------------------

        //Check class attributes
        assertEquals("true", jobClassAttributes.get("class.persistant"));

        assertEquals("true", jobClassAttributes.get("class.secure"));

        //check field attributes
        assertEquals("true", jobClassAttributes.get("field.description.persistant"));

        assertEquals("true", jobClassAttributes.get("field.boss.bastard"));

        //check method attributes
        assertEquals("true", jobClassAttributes.get("method.fireAllEmployees().secure"));
        assertEquals("false", jobClassAttributes.get("method.hireEmployee(String,Employee).secure"));
    }

}