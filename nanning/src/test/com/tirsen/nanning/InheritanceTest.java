package com.tirsen.nanning;

import junit.framework.TestCase;
import com.tirsen.nanning.definition.AspectClass;
import com.tirsen.nanning.definition.AspectDefinition;

public class InheritanceTest extends TestCase {
    public static interface InheritedIntf extends Intf
    {
    }

    public static class InheritedImpl extends Impl implements InheritedIntf {
    }

    public static interface InheritedSideAspect extends Intf, SideAspect
    {
    }

    public static class InheritedSideAspectImpl extends SideAspectImpl implements InheritedSideAspect
    {
        public void call() {
            fail("should never be called");
        }
    }

    public void testInheritance()
    {
        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(InheritedIntf.class);
        aspectClass.addInterceptor(MockInterceptor.class);
        aspectClass.setTarget(InheritedImpl.class);
        AspectDefinition aspectDefinition = new AspectDefinition();
        // note that this interface also extends Intf, but that will never get called since
        // the class-aspect will take precedence
        aspectDefinition.setInterface(InheritedSideAspect.class);
        aspectDefinition.setTarget(InheritedSideAspectImpl.class);
        aspectClass.addAspect(aspectDefinition);
        InheritedIntf proxy = (InheritedIntf) aspectClass.newInstance();

        MockInterceptor classInterceptor = (MockInterceptor) Aspects.getInterceptors(proxy)[0];
        classInterceptor.expectTarget(Aspects.getTarget(proxy, Intf.class));

        proxy.call();
    }
}
