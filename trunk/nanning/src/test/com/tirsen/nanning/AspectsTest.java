/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import junit.framework.TestCase;
import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.definition.AspectClass;
import com.tirsen.nanning.definition.AspectRepository;

/**
 * TODO document AspectsTest
 *
 * <!-- $Id: AspectsTest.java,v 1.2 2003-01-12 13:25:40 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
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
        assertSame("did not change target", other, Aspects.getTarget(proxy, Intf.class));
    }
}
