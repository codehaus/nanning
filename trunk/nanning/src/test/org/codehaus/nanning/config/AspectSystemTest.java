package org.codehaus.nanning.config;

import java.util.Iterator;

import org.codehaus.nanning.*;
import junit.framework.TestCase;

public class AspectSystemTest extends TestCase {

    public void test() throws NoSuchMethodException {
        AspectSystem aspectSystem = new AspectSystem();
        aspectSystem.addAspect(new FindTargetMixinAspect());
        aspectSystem.addAspect(new MixinAspect(TestMixin.class, TestMixinImpl.class, P.all()));
        aspectSystem.addAspect(new InterceptorAspect(new MockInterceptor()));
        final NullInterceptor nullInterceptor = new NullInterceptor();
        aspectSystem.addAspect(new InterceptorAspect(nullInterceptor));
        final MockConstructionInterceptor constructionInterceptor = new MockConstructionInterceptor();

        aspectSystem.addAspect(new Aspect() {
            public void introduce(AspectInstance aspectInstance) {
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
        aspectSystem.addAspect(new InterceptorAspect(NullInterceptor.class, InterceptorAspect.PER_METHOD));
        Object bigMomma = aspectSystem.newInstance(IntfSub.class);

        assertEquals(1,
                     Aspects.getAspectInstance(bigMomma).getMixinForInterface(Intf.class).
                     getInterceptorsForMethod(Intf.class.getMethod("call", new Class[0])).size());
    }

    public static interface Base {
        public void m();
    }

    public static interface Sub extends Base {
    }

    public static interface C extends Sub, Base {
    }
}
