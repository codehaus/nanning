/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.test;

import junit.framework.Assert;

/**
 * TODO document SideAspectImpl
 *
 * <!-- $Id: SideAspectImpl.java,v 1.1 2002-10-22 18:28:09 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public class SideAspectImpl implements SideAspect
{
    private boolean called;

    public void sideCall()
    {
        called = true;
    }

    public void verify()
    {
        Assert.assertTrue(called);
    }
}
