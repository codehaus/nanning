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
 * TODO document Impl
 *
 * <!-- $Id: Impl.java,v 1.3 2003-03-12 22:34:55 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.3 $
 */
public class Impl implements Intf, Serializable
{
    private boolean called;
    private Object expectThis;
    private Object actualThis;

    public void expectThis(Object expectThis)
    {
        this.expectThis = expectThis;
    }

    public void call()
    {
        called = true;
        actualThis = Aspects.getThis();
    }

    public void verify()
    {
        Assert.assertTrue(called);
        if (expectThis != null)
        {
            Assert.assertSame(expectThis, actualThis);
        }
    }
}
