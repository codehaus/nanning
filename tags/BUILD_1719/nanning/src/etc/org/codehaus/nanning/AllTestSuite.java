/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.codehaus.nanning;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * TODO document AllTestSuite
 *
 * <!-- $Id: AllTestSuite.java,v 1.1 2003-07-04 10:53:56 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.1 $
 */
public class AllTestSuite {
    ///CLOVER:OFF
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(AcceptanceTestSuite.suite());
        suite.addTest(UnitTestSuite.suite());
        return suite;
    }
    ///CLOVER:ON
}
