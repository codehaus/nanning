/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import com.tirsen.nanning.samples.ContractInterceptorTest;
import com.tirsen.nanning.samples.TraceInterceptorTest;
import com.tirsen.nanning.samples.prevayler.PrevaylerTest;
import com.tirsen.nanning.samples.rmi.RemoteTest;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * TODO document UnitTestSuite
 *
 * <!-- $Id: AcceptanceTestSuite.java,v 1.5 2003-03-21 17:11:13 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.5 $
 */
public class AcceptanceTestSuite {
    ///CLOVER:OFF
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(PerformanceTest.class);
        suite.addTestSuite(PrevaylerTest.class);
        suite.addTestSuite(TraceInterceptorTest.class);
        suite.addTestSuite(RemoteTest.class);
        suite.addTestSuite(ContractInterceptorTest.class);
        return suite;
    }
    ///CLOVER:ON
}
