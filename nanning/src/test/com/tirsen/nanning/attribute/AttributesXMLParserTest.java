package com.tirsen.nanning.attribute;

/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

import junit.framework.TestCase;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * TODO document AttributesTagHandlerTest
 *
 * <!-- $Id: AttributesXMLParserTest.java,v 1.2 2003-01-19 22:47:08 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
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