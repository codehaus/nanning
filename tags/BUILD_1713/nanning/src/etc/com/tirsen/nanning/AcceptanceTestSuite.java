/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import com.tirsen.nanning.prevayler.PrevaylerTest;
import com.tirsen.nanning.xml.XMLTest;
import com.tirsen.nanning.attribute.AttributesTest;
import com.tirsen.nanning.remote.RemoteCallServerTest;
import com.tirsen.nanning.trace.TraceInterceptorTest;
import com.tirsen.nanning.contract.ContractInterceptorTest;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * TODO document UnitTestSuite
 *
 * <!-- $Id: AcceptanceTestSuite.java,v 1.8 2003/05/22 20:18:35 tirsen Exp $ -->
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
        suite.addTestSuite(AttributesTest.class);

        suite.addTestSuite(com.tirsen.nanning.locking.AcceptanceTest.class);

        return suite;
    }
    ///CLOVER:ON
}
