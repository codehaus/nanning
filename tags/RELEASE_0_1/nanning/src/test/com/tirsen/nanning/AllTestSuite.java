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
 * TODO document AllTestSuite
 *
 * <!-- $Id: AllTestSuite.java,v 1.1 2002-12-11 10:57:52 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.1 $
 */
public class AllTestSuite
{
    ///CLOVER:OFF
    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        suite.addTest(AcceptanceTestSuite.suite());
        suite.addTest(UnitTestSuite.suite());
        return suite;
    }
    ///CLOVER:ON
}
