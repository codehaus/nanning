package com.tirsen.nanning;

import junit.framework.TestCase;

public class ConstructionInterceptorTest extends TestCase {
    public void testConstructionInterceptor() {
        AspectInstance aspectInstance = new AspectInstance(Intf.class);
        aspectInstance.addMixin(new MixinInstance(Intf.class, new IntfImpl()));
        MockConstructionInterceptor mockConstructionInterceptor = new MockConstructionInterceptor();
        aspectInstance.addConstructionInterceptor(mockConstructionInterceptor);

        IntfImpl newTarget = new IntfImpl();
        mockConstructionInterceptor.changeTarget(newTarget);

        Intf intf = (Intf) aspectInstance.getProxy();
        ConstructionInvocation constructionInvocation = mockConstructionInterceptor.getInvocation();
        mockConstructionInterceptor.verify();
        assertSame("proxy was not correct", intf, constructionInvocation.getProxy());
        assertSame("target was not correct", Aspects.getTargets(intf)[0], constructionInvocation.getTarget());
    }
}
