package com.tirsen.nanning.config;

import com.tirsen.nanning.*;
import junit.framework.TestCase;

public class AspectSystemTest extends TestCase {

    public void test() throws NoSuchMethodException {
        AspectSystem aspectSystem = new AspectSystem();
        aspectSystem.addAspect(new FindTargetMixinAspect());
        aspectSystem.addAspect(new Introductor(TestMixin.class, TestMixinImpl.class));
        aspectSystem.addAspect(new Aspect() {
            private MockInterceptor mockInterceptor = new MockInterceptor();

            public void adviseMixin(AspectInstance aspectInstance, MixinInstance mixin) {
                mixin.addInterceptor(mockInterceptor);
            }

            public void advise(AspectInstance aspectInstance) {
            }

            public void introduce(AspectInstance aspectInstance) {
            }
        });
        final NullInterceptor nullInterceptor = new NullInterceptor();
        aspectSystem.addAspect(new Aspect() {
            public void adviseMixin(AspectInstance aspectInstance, MixinInstance mixin) {
                mixin.addInterceptor(nullInterceptor);
            }

            public void advise(AspectInstance aspectInstance) {
            }

            public void introduce(AspectInstance aspectInstance) {
            }
        });
        final MockConstructionInterceptor constructionInterceptor = new MockConstructionInterceptor();

        aspectSystem.addAspect(new Aspect() {
            public void introduce(AspectInstance aspectInstance) {
            }

            public void adviseMixin(AspectInstance aspectInstance, MixinInstance mixin) {
            }

            public void advise(AspectInstance aspectInstance) {
                aspectInstance.addConstructionInterceptor(constructionInterceptor);
            }
        });

        Object bigMomma = aspectSystem.newInstance(Intf.class);
        assertTrue(Aspects.isAspectObject(bigMomma));
        assertTrue(bigMomma instanceof Intf);
        assertTrue(bigMomma instanceof TestMixin);

        assertEquals(2, Aspects.getInterceptors(bigMomma).size());

        constructionInterceptor.verify();

        assertSame(nullInterceptor,
                   Aspects.getAspectInstance(bigMomma).getMixinForInterface(Intf.class).
                   getInterceptorsForMethod(Intf.class.getMethod("call", new Class[0])).get(1));
        MockInterceptor callInterceptor =
                (MockInterceptor) Aspects.getAspectInstance(bigMomma).getMixinForInterface(Intf.class).
                getInterceptorsForMethod(Intf.class.getMethod("call", new Class[0])).get(0);
        MockInterceptor mixinCallInterceptor =
                (MockInterceptor) Aspects.getAspectInstance(bigMomma).getMixinForInterface(TestMixin.class).
                getInterceptorsForMethod(TestMixin.class.getMethod("mixinCall", new Class[0])).get(0);
        ((TestMixin) bigMomma).mixinCall();
        mixinCallInterceptor.verify();
        ((Intf) bigMomma).call();
        callInterceptor.verify();
    }

    public static interface IntfSub extends Intf {
    }

    public static class IntfSubImpl extends IntfImpl {
    }

    public void testInheritance() throws NoSuchMethodException {
        AspectSystem aspectSystem = new AspectSystem();
        aspectSystem.addAspect(new FindTargetMixinAspect());
        aspectSystem.addAspect(new Aspect() {
            public void adviseMixin(AspectInstance aspectInstance, MixinInstance mixin) {
                mixin.addInterceptor(new NullInterceptor());
            }

            public void advise(AspectInstance aspectInstance) {
            }

            public void introduce(AspectInstance aspectInstance) {
            }
        });
        Object bigMomma = aspectSystem.newInstance(IntfSub.class);

        assertEquals(1,
                     Aspects.getAspectInstance(bigMomma).getMixinForInterface(Intf.class).
                     getInterceptorsForMethod(Intf.class.getMethod("call", new Class[0])).size());
    }

    public void testCreateWithTargets() {
//        AspectSystem aspectSystem = new AspectSystem();
//        aspectSystem.addAspect(new FindTargetMixinAspect());
//        aspectSystem.addAspect(new Introductor(TestMixin.class, TestMixinImpl.class));
//        IntfImpl impl = new IntfImpl();
//        TestMixinImpl testMixin = new TestMixinImpl();
//
//        Object bigMomma = aspectSystem.newInstance(Intf.class, new Object[]{impl, testMixin});
//        assertSame(Aspects.getTarget(bigMomma, Intf.class), impl);
//        assertSame(Aspects.getTarget(bigMomma, TestMixin.class), testMixin);
//
//        // test in another order
//        bigMomma = aspectSystem.newInstance(Intf.class, new Object[]{testMixin, impl});
//        assertSame(Aspects.getTarget(bigMomma, Intf.class), impl);
//        assertSame(Aspects.getTarget(bigMomma, TestMixin.class), testMixin);
//
//        // test one object too many
//        try {
//            bigMomma = aspectSystem.newInstance(Intf.class, new Object[]{testMixin, impl, new Object()});
//            fail();
//        } catch (IllegalArgumentException e) {
//        }
    }

    public static interface Base {
        public void m();
    }

    public static interface Sub extends Base {
    }

    public static interface C extends Sub, Base {
    }
}
