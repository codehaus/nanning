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
 * <!-- $Id: AcceptanceTestSuite.java,v 1.1 2002-12-08 15:32:01 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public class AcceptanceTestSuite
{
    ///CLOVER:OFF
    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(PerformanceTest.class);
        suite.addTestSuite(PrevaylerTest.class);
        suite.addTestSuite(TraceInterceptorTest.class);
        return suite;
    }
    ///CLOVER:ON
}
