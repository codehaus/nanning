/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import junit.framework.Assert;

import java.io.Serializable;

/**
 * TODO document TestMixinImpl
 *
 * <!-- $Id: TestMixinImpl.java,v 1.2 2003-03-12 22:34:55 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
 */
public class TestMixinImpl implements TestMixin, Serializable
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
