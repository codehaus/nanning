/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import com.tirsen.nanning.attribute.AttributesTest;
import com.tirsen.nanning.attribute.AttributesXMLParserTest;
import com.tirsen.nanning.config.AspectSystemTest;
import com.tirsen.nanning.samples.prevayler.ObjectGraphVisitorTest;
import com.tirsen.nanning.samples.rmi.RemoteTest;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * TODO document UnitTestSuite
 *
 * <!-- $Id: UnitTestSuite.java,v 1.16 2003-05-09 14:57:49 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.16 $
 */
public class UnitTestSuite {
    ///CLOVER:OFF
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(AspectInstanceTest.class);
        suite.addTestSuite(AttributesTest.class);
        suite.addTestSuite(AttributesXMLParserTest.class);
        suite.addTestSuite(ObjectGraphVisitorTest.class);
        suite.addTestSuite(AspectSystemTest.class);
        suite.addTestSuite(SerializationTest.class);
        suite.addTestSuite(RemoteTest.class);
        return suite;
    }
    ///CLOVER:ON
}
