/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import com.tirsen.nanning.definition.AspectClass;
import com.tirsen.nanning.definition.AspectDefinition;
import junit.framework.TestCase;

/**
 * TODO document AspectClassTest
 *
 * <!-- $Id: AspectClassTest.java,v 1.13 2003-05-11 13:40:52 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.13 $
 */
public class AspectClassTest extends TestCase {
    public static class BlahongaException extends RuntimeException {
    }

    public void testThrowsCorrectExceptions() {
        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(Intf.class);
        aspectClass.addInterceptor(MockInterceptor.class);
        aspectClass.addInterceptor(MockInterceptor.class);
        aspectClass.setTarget(IntfImpl.class);

        Intf proxy = (Intf) aspectClass.newInstance();

        Aspects.setTarget(proxy, Intf.class, new IntfImpl() {
            public void call() {
                throw new BlahongaException();
            }
        });

        try {
            proxy.call();
            fail();
        } catch (BlahongaException shouldHappen) {
        } catch (Exception e) {
            fail();
        }
    }

    public void testSideAspectAndAspectsOnProxy() throws IllegalAccessException, InstantiationException, NoSuchMethodException {
        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(Intf.class);
        aspectClass.addInterceptor(MockInterceptor.class);
        aspectClass.addInterceptor(NullInterceptor.class);
        aspectClass.setTarget(IntfImpl.class);
        AspectDefinition aspectDefinition = new AspectDefinition();
        aspectDefinition.setInterface(TestMixin.class);
        aspectDefinition.addInterceptor(NullInterceptor.class);
        aspectDefinition.addInterceptor(MockInterceptor.class);
        aspectDefinition.setTarget(TestMixinImpl.class);
        aspectClass.addAspect(aspectDefinition);

        Object bigMomma = aspectClass.newInstance();

        assertEquals(4, Aspects.getInterceptors(bigMomma).size());

        verifySideAspect(bigMomma);
    }

    public static void verifySideAspect(Object bigMomma) throws NoSuchMethodException {
        IntfImpl target = (IntfImpl) Aspects.getTarget(bigMomma, Intf.class);
        target.expectThis(bigMomma);
        MockInterceptor classInterceptor =
                (MockInterceptor) (Aspects.getInterceptors(bigMomma, Intf.class.getMethod("call", null))[0]);
        classInterceptor.expectAtIndex(0);
        classInterceptor.expectNumberOfInterceptors(2);
        classInterceptor.expectCalledTimes(1);
        classInterceptor.expectProxy(bigMomma);
        classInterceptor.expectMethod(Intf.class.getMethod("call", null));
        classInterceptor.expectTarget(target);

        TestMixinImpl sideTarget = (TestMixinImpl) Aspects.getTarget(bigMomma, TestMixin.class);
        MockInterceptor sideInterceptor =
                (MockInterceptor) (Aspects.getInterceptors(bigMomma, TestMixin.class.getMethod("mixinCall", null))[1]);
        sideInterceptor.expectAtIndex(1);
        sideInterceptor.expectNumberOfInterceptors(2);
        sideInterceptor.expectCalledTimes(1);
        sideInterceptor.expectProxy(bigMomma);
        sideInterceptor.expectMethod(TestMixin.class.getMethod("mixinCall", null));
        sideInterceptor.expectTarget(sideTarget);

        // this calls the class-target and the class-interceptor
        ((Intf) bigMomma).call();
        // this calls the side-target, the class-interceptor and the side-interceptor
        classInterceptor.expectTarget(null);
        classInterceptor.expectMethod(null);
        classInterceptor.expectNumberOfInterceptors(2);
        ((TestMixin) bigMomma).mixinCall();

        classInterceptor.verify();
        target.verify();
        sideInterceptor.verify();
        sideTarget.verify();
    }

    public void testNoAspects() throws IllegalAccessException, InstantiationException {
        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(Intf.class);
        aspectClass.setTarget(IntfImpl.class);
        Intf intf = (Intf) aspectClass.newInstance();

        IntfImpl impl = (IntfImpl) Aspects.getTarget(intf, Intf.class);

        intf.call();
        impl.verify();
    }
}
