package org.codehaus.nanning;

import junit.framework.TestCase;

public class InheritanceTest extends TestCase {
    public static interface InheritedIntf extends Intf {
    }

    public static class InheritedImpl extends IntfImpl implements InheritedIntf {
    }

    public static interface InheritedSideAspect extends Intf, TestMixin {
    }

    public static class InheritedSideAspectImpl extends TestMixinImpl implements InheritedSideAspect {
        public void call() {
        }
    }

    public void testInheritance() {
        AspectInstance instance = new AspectInstance();
        Mixin mixin = new Mixin(InheritedIntf.class, new InheritedImpl());
        mixin.addInterceptor(new MockInterceptor());
        instance.addMixin(mixin);

        InheritedIntf proxy = (InheritedIntf) instance.getProxy();

        MockInterceptor classInterceptor = (MockInterceptor) Aspects.getInterceptors(proxy).iterator().next();
        classInterceptor.expectTarget(Aspects.getTarget(proxy, Intf.class));

        proxy.call();
    }
}
