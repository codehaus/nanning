package com.tirsen.nanning;

import junit.framework.TestCase;
import com.tirsen.nanning.definition.AspectClass;
import com.tirsen.nanning.definition.AspectDefinition;
import com.tirsen.nanning.definition.InterceptorDefinition;
import com.tirsen.nanning.definition.SingletonInterceptor;

public class ConstructionInterceptorTest extends TestCase {
    public static class MockConstructionInterceptor implements ConstructionInterceptor, SingletonInterceptor {
        private ConstructionInvocation constructionInvocation;
        private Object newTarget;

        public Object construct(ConstructionInvocation invocation) {
            this.constructionInvocation = invocation;
            if (newTarget != null) {
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

        AspectDefinition aspectDefinition = (AspectDefinition) aspectClass.getAspectDefinitions().iterator().next();
        InterceptorDefinition interceptorDefinition = (InterceptorDefinition) aspectDefinition.getInterceptorDefinitions().iterator().next();
        MockConstructionInterceptor mockConstructionInterceptor = (MockConstructionInterceptor) interceptorDefinition.getSingleton();

        Impl newTarget = new Impl();
        mockConstructionInterceptor.changeTarget(newTarget);

        Intf intf = (Intf) aspectClass.newInstance();
        ConstructionInvocation constructionInvocation = mockConstructionInterceptor.getInvocation();
        assertNotNull("construction-interceptor never called", constructionInvocation);
        assertSame("proxy was not correct", intf, constructionInvocation.getProxy());
        assertSame("target was not correct", Aspects.getTargets(intf)[0], constructionInvocation.getTarget());
    }
}
