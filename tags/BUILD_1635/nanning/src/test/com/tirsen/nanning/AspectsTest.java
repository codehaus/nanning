/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import junit.framework.TestCase;

/**
 * TODO document AspectsTest
 *
 * <!-- $Id: AspectsTest.java,v 1.6 2003-05-11 13:40:52 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.6 $
 */
public class AspectsTest extends TestCase {
    public static class OtherImpl extends IntfImpl {
    }

    public void testSetTarget() {
        AspectInstance instance = new AspectInstance();
        MixinInstance mixin = new MixinInstance(Intf.class, new IntfImpl());
        instance.addMixin(mixin);
        Object proxy = instance.getProxy();

        assertTrue(Aspects.getTarget(proxy, Intf.class) instanceof IntfImpl);

        OtherImpl other = new OtherImpl();
        Aspects.setTarget(proxy, Intf.class, other);
        assertSame("did not change target", other, Aspects.getTarget(proxy, Intf.class));
    }
}
