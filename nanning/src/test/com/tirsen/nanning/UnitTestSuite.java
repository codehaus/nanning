/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import com.tirsen.nanning.attribute.AttributesTest;
import com.tirsen.nanning.attribute.AttributesXMLParserTest;
import com.tirsen.nanning.samples.prevayler.ObjectGraphVisitorTest;
import com.tirsen.nanning.config.Def2Test;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * TODO document UnitTestSuite
 *
 * <!-- $Id: UnitTestSuite.java,v 1.11 2003-02-10 20:01:08 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.11 $
 */
public class UnitTestSuite {
    ///CLOVER:OFF
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(AspectClassTest.class);
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
        return suite;
    }
    ///CLOVER:ON
}
