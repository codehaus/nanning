/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import com.tirsen.nanning.samples.test.LogInterceptorTest;

/**
 * TODO document UnitTestSuite
 *
 * <!-- $Id: UnitTestSuite.java,v 1.5 2002-10-28 21:45:34 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.5 $
 */
public class UnitTestSuite
{
    ///CLOVER:OFF
    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(AspectClassTest.class);
        suite.addTestSuite(AspectRepositoryTest.class);
        suite.addTestSuite(PerformanceTest.class);
        suite.addTestSuite(LogInterceptorTest.class);
        suite.addTestSuite(AttributesTest.class);
        return suite;
    }
    ///CLOVER:ON
}
