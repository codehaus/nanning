/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import junit.framework.Assert;

/**
 * TODO document Impl
 *
 * <!-- $Id: Impl.java,v 1.2 2003-01-24 13:29:30 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
 */
public class Impl implements Intf
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
