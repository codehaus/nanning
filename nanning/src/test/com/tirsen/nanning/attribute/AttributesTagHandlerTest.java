package com.tirsen.nanning.attribute;

/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.xml.sax.SAXException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * TODO document AttributesTagHandlerTest
 *
 * <!-- $Id: AttributesTagHandlerTest.java,v 1.1 2003-01-12 13:25:40 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public class AttributesTagHandlerTest extends TestCase {
    private SAXParser parser;
    private InputStream is;
    private Map attributesMap;
    private static final String SCHOOL_CLASS = "com.tirsen.nanning.School";
    private static final String JOB_CLASS = "com.tirsen.nanning.Job";
    private static final String DOT = ".";
    private static final String CLASS_ATTRIB = "classAttrib";
    private static final String CLASS = "class";
    private Properties jobClassAttributes;


    protected void setUp() throws Exception {

        super.setUp();

        parser = SAXParserFactory.newInstance().newSAXParser();
        is = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/tirsen/nanning/attribute/Job.xml");

        jobClassAttributes = AttributesTagHandler.parseXML(is);

    }

    protected void tearDown() throws Exception {
        super.tearDown();

    }

    public void testAttributes() throws IOException, NoSuchMethodException, NoSuchFieldException {
        StringBuffer sb;

        //---- CHECK com.tirsen.nanning.Job ------------------

        //Check class attributes
        sb = new StringBuffer();
        sb.append(CLASS).append(DOT).append("persistant");
        assertEquals("true", jobClassAttributes.get(sb.toString()));

        sb = new StringBuffer();
        sb.append(CLASS).append(DOT).append("secure");
        assertEquals("true", jobClassAttributes.get(sb.toString()));

        //check field attributes
        sb = new StringBuffer();
        sb.append("description").append(DOT).append("persistant");
        assertEquals("true", jobClassAttributes.get(sb.toString()));

        sb = new StringBuffer();
        sb.append("boss").append(DOT).append("bastard");
        assertEquals("true", jobClassAttributes.get(sb.toString()));

        //check method attributes
        sb = new StringBuffer();
        sb.append("fireAllEmployees()").append(DOT).append("secure");
        assertEquals("true", jobClassAttributes.get(sb.toString()));

        sb = new StringBuffer();
        sb.append("hireEmployee(Employee)").append(DOT).append("secure");
        assertEquals("false", jobClassAttributes.get(sb.toString()));
    }

}