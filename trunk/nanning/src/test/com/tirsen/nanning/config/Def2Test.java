package com.tirsen.nanning.config;

import com.tirsen.nanning.*;
import junit.framework.TestCase;

public class Def2Test extends TestCase {
    public void test() throws NoSuchMethodException {
        AspectSystem aspectSystem = new AspectSystem();
        aspectSystem.addAspect(AspectSystem.mixin(Intf.class, Impl.class));
        aspectSystem.addPointcut(new AllPointcut(new AddMixinAdvise(TestMixin.class, TestMixinImpl.class)));
        aspectSystem.addAspect(AspectSystem.interceptor(MockInterceptor.class));
        NullInterceptor nullInterceptor = new NullInterceptor();
        aspectSystem.addAspect(AspectSystem.interceptor(nullInterceptor));
        MockConstructionInterceptor constructionInterceptor = new MockConstructionInterceptor();
        aspectSystem.addPointcut(new AllPointcut(new ConstructionInterceptorAdvise(constructionInterceptor)));

        Object bigMomma = aspectSystem.newInstance(Intf.class);
        assertTrue(Aspects.isAspectObject(bigMomma));
        assertTrue(bigMomma instanceof Intf);
        assertTrue(bigMomma instanceof TestMixin);

        assertEquals(3, Aspects.getInterceptors(bigMomma).length);

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

    public void testCreateWithTargets() {
        AspectSystem aspectSystem = new AspectSystem();
        aspectSystem.addAspect(AspectSystem.mixin(Intf.class, Impl.class));
        aspectSystem.addPointcut(new AllPointcut(new AddMixinAdvise(TestMixin.class, TestMixinImpl.class)));
        Impl impl = new Impl();
        TestMixinImpl testMixin = new TestMixinImpl();

        Object bigMomma = aspectSystem.newInstance(Intf.class, new Object[]{impl, testMixin});
        assertSame(Aspects.getTarget(bigMomma, Intf.class), impl);
        assertSame(Aspects.getTarget(bigMomma, TestMixin.class), testMixin);

        // test in another order
        bigMomma = aspectSystem.newInstance(Intf.class, new Object[]{testMixin, impl});
        assertSame(Aspects.getTarget(bigMomma, Intf.class), impl);
        assertSame(Aspects.getTarget(bigMomma, TestMixin.class), testMixin);

        // test one object too many
        try {
            bigMomma = aspectSystem.newInstance(Intf.class, new Object[]{testMixin, impl, new Object()});
            fail();
        } catch (IllegalArgumentException e) {
        }
    }
}
