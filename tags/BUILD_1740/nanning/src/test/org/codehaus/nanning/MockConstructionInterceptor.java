package org.codehaus.nanning;

import junit.framework.Assert;

public class MockConstructionInterceptor implements ConstructionInterceptor {
    private ConstructionInvocation constructionInvocation;
    private Object newTarget;

    public Object construct(ConstructionInvocation invocation) {
        this.constructionInvocation = invocation;
        if (newTarget != null) {
            invocation.setTarget(newTarget);
        }
        return invocation.getProxy();
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
