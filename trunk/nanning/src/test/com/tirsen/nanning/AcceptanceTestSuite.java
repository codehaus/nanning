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
import com.tirsen.nanning.samples.rmi.RemoteCallServerTest;
import com.tirsen.nanning.xml.XMLTest;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * TODO document UnitTestSuite
 *
 * <!-- $Id: AcceptanceTestSuite.java,v 1.8 2003-05-22 20:18:35 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.8 $
 */
public class AcceptanceTestSuite {
    ///CLOVER:OFF
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(PrevaylerTest.class);
        suite.addTestSuite(TraceInterceptorTest.class);
        suite.addTestSuite(RemoteCallServerTest.class);
        suite.addTestSuite(ContractInterceptorTest.class);
        suite.addTestSuite(XMLTest.class);
        return suite;
    }
    ///CLOVER:ON
}
