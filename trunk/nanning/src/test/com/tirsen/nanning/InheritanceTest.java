package com.tirsen.nanning;

import com.tirsen.nanning.definition.AspectClass;
import com.tirsen.nanning.definition.AspectDefinition;
import junit.framework.TestCase;

public class InheritanceTest extends TestCase {
    public static interface InheritedIntf extends Intf {
    }

    public static class InheritedImpl extends Impl implements InheritedIntf {
    }

    public static interface InheritedSideAspect extends Intf, TestMixin {
    }

    public static class InheritedSideAspectImpl extends TestMixinImpl implements InheritedSideAspect {
        public void call() {
        }
    }

    public void testInheritance() {
        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(InheritedIntf.class);
        aspectClass.addInterceptor(MockInterceptor.class);
        aspectClass.setTarget(InheritedImpl.class);

        AspectDefinition aspectDefinition = new AspectDefinition();
        aspectDefinition.setInterface(InheritedSideAspect.class);
        aspectDefinition.setTarget(InheritedSideAspectImpl.class);
        aspectClass.addAspect(aspectDefinition);

        InheritedIntf proxy = (InheritedIntf) aspectClass.newInstance();

        MockInterceptor classInterceptor = (MockInterceptor) Aspects.getInterceptors(proxy)[0];
        classInterceptor.expectTarget(Aspects.getTarget(proxy, Intf.class));

        proxy.call();
    }
}
