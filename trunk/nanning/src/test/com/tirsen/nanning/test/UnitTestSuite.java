/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TODO document UnitTestSuite
 *
 * <!-- $Id: UnitTestSuite.java,v 1.1 2002-10-21 21:07:31 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public class UnitTestSuite
{
    ///CLOVER:OFF
    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(AspectProxyTest.class);
        suite.addTestSuite(FactoryTest.class);
        suite.addTestSuite(PerformanceTest.class);
        return suite;
    }
    ///CLOVER:ON
}
