package com.tirsen.nanning;

import com.tirsen.nanning.definition.SingletonInterceptor;
import junit.framework.Assert;

public class MockConstructionInterceptor implements ConstructionInterceptor, SingletonInterceptor {
    private ConstructionInvocation constructionInvocation;
    private Object newTarget;

    public Object construct(ConstructionInvocation invocation) {
        this.constructionInvocation = invocation;
        if (newTarget != null) {
            invocation.setTarget(newTarget);
        }
        return invocation.getProxy();
    }

    public boolean interceptsConstructor(Class klass) {
        return true;
    }

    public ConstructionInvocation getInvocation() {
        return constructionInvocation;
    }

    public void changeTarget(Object newTarget) {
        this.newTarget = newTarget;
    }

    public void verify() {
        Assert.assertNotNull("construction-interceptor never called", constructionInvocation);
    }
}
