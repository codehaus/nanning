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
 * <!-- $Id: UnitTestSuite.java,v 1.7 2002-12-08 15:50:58 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.7 $
 */
public class UnitTestSuite
{
    ///CLOVER:OFF
    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(AspectClassTest.class);
//        suite.addTestSuite(InterceptorTest.class);
//        suite.addTestSuite(ConstructionInterceptorTest.class);
        suite.addTestSuite(AspectRepositoryTest.class);
        suite.addTestSuite(AttributesTest.class);
        suite.addTestSuite(AspectsTest.class);
        suite.addTestSuite(ContractInterceptorTest.class);
        suite.addTestSuite(MethodFilterTest.class);
        suite.addTestSuite(AttributeFilterTest.class);
        return suite;
    }
    ///CLOVER:ON
}