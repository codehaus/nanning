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
 * <!-- $Id: AllTestSuite.java,v 1.2 2003-01-24 13:29:30 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
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
