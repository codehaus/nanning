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
 * TODO document Impl
 *
 * <!-- $Id: Impl.java,v 1.4 2003-03-21 17:11:14 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.4 $
 */
public class Impl implements Intf, Serializable {
    private boolean called;
    private Object expectThis;
    private Object actualThis;

    public void expectThis(Object expectThis) {
        this.expectThis = expectThis;
    }

    public void call() {
        called = true;
        actualThis = Aspects.getThis();
    }

    public void verify() {
        Assert.assertTrue(called);
        if (expectThis != null) {
            Assert.assertSame(expectThis, actualThis);
        }
    }
}
