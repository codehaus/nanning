/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import junit.framework.Assert;

/**
 * TODO document SideAspectImpl
 *
 * <!-- $Id: SideAspectImpl.java,v 1.2 2003-01-24 13:29:30 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
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
