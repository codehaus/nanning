/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.definition;

import java.io.Serializable;

import junit.framework.Assert;

/**
 * TODO document TestMixinImpl
 *
 * <!-- $Id: TestMixinImpl.java,v 1.1 2003-07-01 16:08:10 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.1 $
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
