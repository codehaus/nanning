/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import com.tirsen.nanning.definition.AspectClass;
import junit.framework.TestCase;

/**
 * TODO document AspectsTest
 *
 * <!-- $Id: AspectsTest.java,v 1.4 2003-03-21 17:11:14 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.4 $
 */
public class AspectsTest extends TestCase {
    public static class OtherImpl extends Impl {
    }

    public void testSetTarget() {
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
