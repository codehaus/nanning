/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * TODO document AllTestSuite
 *
 * <!-- $Id: AllTestSuite.java,v 1.3 2003-03-21 17:11:13 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.3 $
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
