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
 * <!-- $Id: AspectsTest.java,v 1.7 2003-07-04 06:57:11 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.7 $
 */
public class AspectsTest extends TestCase {

//    public static class HelloWorld {}

    public static interface Interface {}

    public static class OtherImpl extends IntfImpl {
    }

    public void testSetTargetChangesTarget() {
        AspectInstance instance = new AspectInstance();
        MixinInstance mixin = new MixinInstance(Intf.class, new IntfImpl());
        instance.addMixin(mixin);
        Object proxy = instance.getProxy();

        assertTrue(Aspects.getTarget(proxy, Intf.class) instanceof IntfImpl);

        OtherImpl other = new OtherImpl();
        Aspects.setTarget(proxy, Intf.class, other);
        assertSame("did not change target", other, Aspects.getTarget(proxy, Intf.class));
    }

//    public void testIsAndGetAspectInstanceWorkForCGLIBCreatedProxy() {
//        AspectInstance instance = new AspectInstance();
//        instance.addMixin(new MixinInstance(HelloWorld.class, new HelloWorld()));
//        HelloWorld helloWorld = (HelloWorld) instance.getProxy();
//        assertTrue(Aspects.isAspectObject(helloWorld));
//        assertSame(instance, Aspects.getAspectInstance(helloWorld));
//    }

    public void testIsAndGetAspectInstanceWorkForReflectCreatedProxy() {
        AspectInstance instance = new AspectInstance();
        instance.addMixin(new MixinInstance(Interface.class, null));
        Interface o = (Interface) instance.getProxy();
        assertTrue(Aspects.isAspectObject(o));
    }

    public void testIsAndGetAspectInstanceForNotAspectedObject() {
        assertFalse(Aspects.isAspectObject("not aspected"));
        try {
            Aspects.getAspectInstance("not aspected");
            fail();
        } catch (IllegalArgumentException shouldHappen) {
        }
    }
}
