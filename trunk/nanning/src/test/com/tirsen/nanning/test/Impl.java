/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.test;

import junit.framework.Assert;

/**
 * TODO document Impl
 *
 * <!-- $Id: Impl.java,v 1.1 2002-10-21 21:07:31 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public class Impl implements Intf
{
    private boolean called;

    public void call()
    {
        called = true;
    }

    public void verify()
    {
        Assert.assertTrue(called);
    }
}
