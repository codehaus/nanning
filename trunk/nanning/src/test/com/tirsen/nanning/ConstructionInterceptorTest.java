package com.tirsen.nanning;

import junit.framework.TestCase;

public class ConstructionInterceptorTest extends TestCase {
    public static class MockConstructionInterceptor implements ConstructionInterceptor {
        private ConstructionInvocation constructionInvocation;
        private Object newTarget;

        public Object construct(ConstructionInvocation invocation) {
            this.constructionInvocation = invocation;
            if(newTarget != null) {
                invocation.setTarget(newTarget);
            }
            return invocation.getProxy();
        }

        public boolean interceptsConstructor(Class interfaceClass) {
            return true;
        }

        public ConstructionInvocation getInvocation() {
            return constructionInvocation;
        }

        public void changeTarget(Object newTarget) {
            this.newTarget = newTarget;
        }
    }

    public void testConstructionInterceptor() {
        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(Intf.class);
        aspectClass.addInterceptor(MockConstructionInterceptor.class);
        aspectClass.setTarget(Impl.class);

        Intf intf = (Intf) aspectClass.newInstance();
        MockConstructionInterceptor mockConstructionInterceptor =
                (MockConstructionInterceptor) Aspects.getInterceptors(intf)[0];
        ConstructionInvocation constructionInvocation = mockConstructionInterceptor.getInvocation();
        assertNotNull("construction-interceptor never called", constructionInvocation);
        assertSame("proxy was not correct", intf, constructionInvocation.getProxy());
        assertSame("target was not correct", Aspects.getTargets(intf)[0], constructionInvocation.getTarget());

        Impl newTarget = new Impl();
        mockConstructionInterceptor.changeTarget(newTarget);
    }
}
