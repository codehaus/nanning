package org.codehaus.nanning;

import junit.framework.TestCase;

public class ConstructionInterceptorTest extends TestCase {
    public void testConstructionInterceptor() {
        AspectInstance aspectInstance = new AspectInstance(Intf.class);
        IntfImpl target = new IntfImpl();
        aspectInstance.addMixin(new Mixin(Intf.class, target));
        MockConstructionInterceptor mockConstructionInterceptor = new MockConstructionInterceptor();
        aspectInstance.addConstructionInterceptor(mockConstructionInterceptor);

        IntfImpl newTarget = new IntfImpl();
        mockConstructionInterceptor.changeTarget(newTarget);

        Intf intf = (Intf) aspectInstance.getProxy();
        ConstructionInvocation constructionInvocation = mockConstructionInterceptor.getInvocation();
        mockConstructionInterceptor.verify();
        assertSame("proxy was not correct", intf, constructionInvocation.getProxy());
        assertEquals("target was not correct", newTarget, constructionInvocation.getTarget());
    }
}
