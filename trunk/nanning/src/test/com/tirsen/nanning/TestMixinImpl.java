/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.io.Serializable;

import junit.framework.Assert;

/**
 * TODO document TestMixinImpl
 *
 * <!-- $Id: TestMixinImpl.java,v 1.3 2003-03-21 17:11:14 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.3 $
 */
public class TestMixinImpl implements TestMixin, Serializable {
    private boolean called;

    public void mixinCall() {
        called = true;
    }

    public void verify() {
        Assert.assertTrue(called);
    }
}
