package org.codehaus.nanning.config;

import org.codehaus.nanning.AspectException;
import org.codehaus.nanning.AspectInstance;
import org.codehaus.nanning.MethodInterceptor;
import org.codehaus.nanning.MixinInstance;

import java.lang.reflect.Method;

public class InterceptorAspect implements Aspect {
    public static final int SINGLETON = 0;
    public static final int PER_METHOD = SINGLETON + 1;
    public static final int PER_INSTANCE = PER_METHOD + 1;

    private Class interceptorClass;
    private int stateManagement;
    private MethodInterceptor singletonInterceptor;
    private Pointcut pointcut;

    public InterceptorAspect(MethodInterceptor interceptor) {
        this(new AllPointcut(), interceptor);
    }

    public InterceptorAspect(Class interceptorClass, int stateManagement) {
        this(new AllPointcut(), interceptorClass, stateManagement);
    }

    public InterceptorAspect(Pointcut pointcut, Class interceptorClass, int stateManagement) {
        this.stateManagement = stateManagement;
        assert stateManagement == SINGLETON || stateManagement == PER_METHOD || stateManagement == PER_INSTANCE
                : "SINGLETON, PER_METHOD and PER_INSTANCE is supported only, not " + stateManagement;

        if (stateManagement == SINGLETON) {
            singletonInterceptor = createInterceptor();
        }

        setPointcut(pointcut);

        this.interceptorClass = interceptorClass;
    }

    public void setPointcut(Pointcut pointcut) {
        this.pointcut = pointcut;
    }

    public InterceptorAspect(Pointcut pointcut, MethodInterceptor interceptor) {
        this.pointcut = pointcut;
        singletonInterceptor = interceptor;
        stateManagement = SINGLETON;
    }

    public Class getInterceptorClass() {
        return interceptorClass;
    }

    public int getStateManagement() {
        return stateManagement;
    }

    private MethodInterceptor createInterceptor() {
        try {
            return (MethodInterceptor) interceptorClass.newInstance();
        } catch (Exception e) {
            throw new AspectException(e);
        }
    }

    public void introduce(AspectInstance instance) {
    }

    Method[] getMethodsToAdvise(AspectInstance instance, MixinInstance mixin) {
        return pointcut.methodsToAdvise(instance, mixin);
    }

    public void advise(AspectInstance instance) {
        if (stateManagement == SINGLETON) {
            pointcut.advise(instance, singletonInterceptor);

        } else if (stateManagement == PER_INSTANCE) {
            pointcut.advise(instance, createInterceptor());

        } else if (stateManagement == PER_METHOD) {
            pointcut.advise(instance, interceptorClass);

        }
    }

    public Pointcut getPointcut() {
        return pointcut;
    }
}
