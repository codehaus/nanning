package com.tirsen.nanning.samples.prevayler;

import java.lang.reflect.Method;

import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.Interceptor;

public class MockInvocation implements Invocation {
    public MockInvocation() {
    }

    public Object[] getArgs() {
        return new Object[0];
    }

    public AspectInstance getAspectInstance() {
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

    public int getNumberOfInterceptors() {
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
