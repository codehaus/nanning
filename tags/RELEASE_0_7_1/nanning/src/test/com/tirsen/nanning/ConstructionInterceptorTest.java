package com.tirsen.nanning;

import com.tirsen.nanning.definition.AspectClass;
import com.tirsen.nanning.definition.AspectDefinition;
import com.tirsen.nanning.definition.InterceptorDefinition;
import junit.framework.TestCase;

public class ConstructionInterceptorTest extends TestCase {
    public void testConstructionInterceptor() {
        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(Intf.class);
        aspectClass.addInterceptor(MockConstructionInterceptor.class);
        aspectClass.setTarget(IntfImpl.class);

        AspectDefinition aspectDefinition = (AspectDefinition) aspectClass.getAspectDefinitions().iterator().next();
        InterceptorDefinition interceptorDefinition = (InterceptorDefinition) aspectDefinition.getInterceptorDefinitions().iterator().next();
        MockConstructionInterceptor mockConstructionInterceptor = (MockConstructionInterceptor) interceptorDefinition.getSingleton();

        IntfImpl newTarget = new IntfImpl();
        mockConstructionInterceptor.changeTarget(newTarget);

        Intf intf = (Intf) aspectClass.newInstance();
        ConstructionInvocation constructionInvocation = mockConstructionInterceptor.getInvocation();
        mockConstructionInterceptor.verify();
        assertSame("proxy was not correct", intf, constructionInvocation.getProxy());
        assertSame("target was not correct", Aspects.getTargets(intf)[0], constructionInvocation.getTarget());
    }
}
