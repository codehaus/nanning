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

        Interceptor[] interceptors = Aspects.getInterceptors(bigMomma);
        assertEquals(3, interceptors.length);

        constructionInterceptor.verify();

        assertSame(nullInterceptor, interceptors[0]);
        MockInterceptor mockInterceptor2 = (MockInterceptor) interceptors[1];
        MockInterceptor mockInterceptor = (MockInterceptor) interceptors[2];
        ((TestMixin) bigMomma).mixinCall();
        mockInterceptor.verify();
        ((Intf) bigMomma).call();
        mockInterceptor2.verify();
    }

    public void testCreateWithTargets() {
        AspectSystem aspectSystem = new AspectSystem();
        aspectSystem.addAspect(AspectSystem.mixin(Intf.class, Impl.class));
        aspectSystem.addPointcut(new AllPointcut(new AddMixinAdvise(TestMixin.class, TestMixinImpl.class)));
        Impl impl = new Impl();
        TestMixinImpl testMixin = new TestMixinImpl();

        Object bigMomma = aspectSystem.newInstance(Intf.class, new Object[] { impl, testMixin });
        assertSame(Aspects.getTarget(bigMomma, Intf.class), impl);
        assertSame(Aspects.getTarget(bigMomma, TestMixin.class), testMixin);

        // test in another order
        bigMomma = aspectSystem.newInstance(Intf.class, new Object[] { testMixin, impl });
        assertSame(Aspects.getTarget(bigMomma, Intf.class), impl);
        assertSame(Aspects.getTarget(bigMomma, TestMixin.class), testMixin);

        // test one object too many
        try {
            bigMomma = aspectSystem.newInstance(Intf.class, new Object[] { testMixin, impl, new Object() });
            fail();
        } catch (IllegalArgumentException e) {
        }
    }
}
