/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import junit.framework.Test;
import junit.framework.TestSuite;
import com.tirsen.nanning.samples.TraceInterceptorTest;
import com.tirsen.nanning.samples.ContractInterceptorTest;
import com.tirsen.nanning.samples.prevayler.PrevaylerTest;
import com.tirsen.nanning.*;


/**
 * TODO document UnitTestSuite
 *
 * <!-- $Id: UnitTestSuite.java,v 1.5 2002-12-03 17:21:02 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.5 $
 */
public class UnitTestSuite
{
    ///CLOVER:OFF
    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(AspectClassTest.class);
        suite.addTestSuite(InterceptorTest.class);
        suite.addTestSuite(ConstructionInterceptorTest.class);
        suite.addTestSuite(AspectRepositoryTest.class);
        suite.addTestSuite(PerformanceTest.class);
        suite.addTestSuite(AttributesTest.class);
        suite.addTestSuite(TraceInterceptorTest.class);
        suite.addTestSuite(AspectsTest.class);
        suite.addTestSuite(ContractInterceptorTest.class);
        suite.addTestSuite(MethodFilterTest.class);
        suite.addTestSuite(com.tirsen.nanning.samples.prevayler.PrevaylerTest.class);
        return suite;
    }
    ///CLOVER:ON
}
