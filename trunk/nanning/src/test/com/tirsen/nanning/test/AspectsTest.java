/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.test;

import junit.framework.TestCase;
import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.AspectClass;

/**
 * TODO document AspectsTest
 *
 * <!-- $Id: AspectsTest.java,v 1.1 2002-10-30 21:39:27 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public class AspectsTest extends TestCase
{
    public static class OtherImpl extends Impl
    {
    }

    public void testSetTarget()
    {
        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(Intf.class);
        aspectClass.setTarget(Impl.class);
        Object proxy = aspectClass.newInstance();

        assertTrue(Aspects.getTarget(proxy, Intf.class) instanceof Impl);

        OtherImpl other = new OtherImpl();
        Aspects.setTarget(proxy, Intf.class, other);
        assertSame(other, Aspects.getTarget(proxy, Intf.class));
    }
}
