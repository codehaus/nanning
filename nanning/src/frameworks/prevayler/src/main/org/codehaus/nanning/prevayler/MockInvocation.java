package org.codehaus.nanning.prevayler;

import java.lang.reflect.Method;

import org.codehaus.nanning.Invocation;
import org.codehaus.nanning.AspectInstance;
import org.codehaus.nanning.Interceptor;

public class MockInvocation implements Invocation {
    public MockInvocation() {
    }

    public Object[] getArgs() {
        return new Object[0];
    }

    public AspectInstance getAspectInstance() {
        return null;
    }

    public int getArgumentCount() {
        return 0;
    }

    public Object getArgument(int arg) {
        return null;
    }

    public int getCurrentIndex() {
        return 0;
    }

    public Interceptor getInterceptor(int index) {
        return null;
    }

    public Method getMethod() {
        return null;
    }

    public int getInterceptorCount() {
        return 0;
    }

    public Object getProxy() {
        return null;
    }

    public Object getTarget() {
        return null;
    }

    public Class getTargetInterface() {
        return null;
    }

    public Object invokeNext() throws Throwable {
        return null;
    }

    public void setTarget(Object o) {
    }
}
