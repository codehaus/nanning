package com.tirsen.nanning.config;

import java.lang.reflect.Method;

import com.tirsen.nanning.*;
import com.tirsen.nanning.attribute.Attributes;
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

    public static interface IntfSub extends Intf {
    }

    public static class ImplSub extends Impl {
    }

    public void testInheritance() throws NoSuchMethodException {
        AspectSystem aspectSystem = new AspectSystem();
        aspectSystem.addAspect(AspectSystem.mixin(IntfSub.class, ImplSub.class));
        aspectSystem.addAspect(AspectSystem.interceptor(new NullInterceptor()));
        Object bigMomma = aspectSystem.newInstance(IntfSub.class);

        assertEquals(1,
                     Aspects.getAspectInstance(bigMomma).getMixinForInterface(Intf.class).
                     getInterceptorsForMethod(Intf.class.getMethod("call", new Class[0])).size());
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

    public static interface Base {
        public void m();
    }

    public static interface Sub extends Base {
    }

    public static interface C extends Sub, Base {
    }

    public void testGetAllMethods() throws NoSuchMethodException {
        assertEquals(12, PointcutAspect.getAllMethods(Object.class).length);
        assertEquals(1, PointcutAspect.getAllMethods(Base.class).length);
        assertEquals(1, PointcutAspect.getAllMethods(Sub.class).length);
        assertEquals(1, PointcutAspect.getAllMethods(C.class).length);
        
        assertEquals(1, PointcutAspect.getAllMethods(C.class).length);
    }
}
