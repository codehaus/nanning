/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import com.tirsen.nanning.attribute.AttributesTest;
import com.tirsen.nanning.attribute.AttributesXMLParserTest;
import com.tirsen.nanning.config.Def2Test;
import com.tirsen.nanning.samples.CacheTest;
import com.tirsen.nanning.samples.prevayler.ObjectGraphVisitorTest;
import com.tirsen.nanning.samples.rmi.RemoteTest;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * TODO document UnitTestSuite
 *
 * <!-- $Id: UnitTestSuite.java,v 1.15 2003-04-23 20:44:37 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.15 $
 */
public class UnitTestSuite {
    ///CLOVER:OFF
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(AspectClassTest.class);
        suite.addTestSuite(AspectInstanceTest.class);
        suite.addTestSuite(InterceptorTest.class);
        suite.addTestSuite(ConstructionInterceptorTest.class);
        suite.addTestSuite(AspectRepositoryTest.class);
        suite.addTestSuite(AttributesTest.class);
        suite.addTestSuite(AspectsTest.class);
        suite.addTestSuite(MethodFilterTest.class);
        suite.addTestSuite(AttributesXMLParserTest.class);
        suite.addTestSuite(AttributeFilterTest.class);
        suite.addTestSuite(ObjectGraphVisitorTest.class);
        suite.addTestSuite(InheritanceTest.class);
        suite.addTestSuite(Def2Test.class);
        suite.addTestSuite(SerializationTest.class);
        suite.addTestSuite(CacheTest.class);
        suite.addTestSuite(RemoteTest.class);
        return suite;
    }
    ///CLOVER:ON
}
