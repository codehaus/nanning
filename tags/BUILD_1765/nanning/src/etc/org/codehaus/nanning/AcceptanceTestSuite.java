/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.codehaus.nanning;

import org.codehaus.nanning.prevayler.PrevaylerTest;
import org.codehaus.nanning.xml.XMLTest;
import org.codehaus.nanning.attribute.AttributesTest;
import org.codehaus.nanning.remote.RemoteCallServerTest;
import org.codehaus.nanning.trace.TraceInterceptorTest;
import org.codehaus.nanning.contract.ContractInterceptorTest;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * TODO document UnitTestSuite
 *
 * <!-- $Id: AcceptanceTestSuite.java,v 1.1 2003-07-04 10:53:56 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.1 $
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

        suite.addTestSuite(org.codehaus.nanning.locking.AcceptanceTest.class);

        return suite;
    }
    ///CLOVER:ON
}
