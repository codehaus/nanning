/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.test;

import junit.framework.Assert;
import com.tirsen.nanning.Aspects;

/**
 * TODO document Impl
 *
 * <!-- $Id: Impl.java,v 1.2 2002-11-03 18:45:47 tirsen Exp $ -->
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
