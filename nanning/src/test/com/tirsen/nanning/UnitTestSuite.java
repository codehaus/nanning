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
import com.tirsen.nanning.samples.CacheTest;
import com.tirsen.nanning.samples.CacheInterceptorTest;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * TODO document UnitTestSuite
 *
 * <!-- $Id: UnitTestSuite.java,v 1.17 2003-05-11 13:40:52 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.17 $
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
        suite.addTestSuite(AspectClassTest.class);
        suite.addTestSuite(ConstructionInterceptorTest.class);
        suite.addTestSuite(InheritanceTest.class);
        suite.addTestSuite(InterceptorTest.class);
        suite.addTestSuite(SerializationTest.class);
        suite.addTestSuite(AttributeFilterTest.class);
        suite.addTestSuite(AspectRepositoryTest.class);
        suite.addTestSuite(AspectsTest.class);
        suite.addTestSuite(AttributeFilterTest.class);
        suite.addTestSuite(MethodFilterTest.class);

        suite.addTestSuite(CacheTest.class);
        suite.addTestSuite(CacheInterceptorTest.class);
        
        suite.addTestSuite(RemoteTest.class);
        return suite;
    }
    ///CLOVER:ON
}
