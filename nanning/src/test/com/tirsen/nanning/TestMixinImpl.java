/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import junit.framework.Assert;

/**
 * TODO document TestMixinImpl
 *
 * <!-- $Id: TestMixinImpl.java,v 1.1 2003-02-06 20:33:42 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public class TestMixinImpl implements TestMixin
{
    private boolean called;

    public void mixinCall()
    {
        called = true;
    }

    public void verify()
    {
        Assert.assertTrue(called);
    }
}
