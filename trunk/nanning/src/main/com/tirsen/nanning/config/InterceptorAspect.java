package com.tirsen.nanning.config;

import com.tirsen.nanning.AspectException;
import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MethodInterceptor;
import com.tirsen.nanning.MixinInstance;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Arrays;

public class InterceptorAspect implements Aspect {
    public static final int SINGLETON = 0;
    public static final int PER_METHOD = SINGLETON + 1;
//    public static final int PER_INSTANCE = PER_METHOD + 1;
//    public static final int PER_MIXIN = PER_INSTANCE + 1;

    private Class interceptorClass;
    private int stateManagement;
    private MethodInterceptor singletonInterceptor;
    private final Pointcut pointcut;

    public InterceptorAspect(MethodInterceptor interceptor) {
        this(new AllPointcut(), interceptor);
    }

    public InterceptorAspect(Class interceptorClass, int stateManagement) {
        this(new AllPointcut(), interceptorClass, stateManagement);
    }

    public InterceptorAspect(Pointcut pointcut, Class interceptorClass, int stateManagement) {
        this.pointcut = pointcut;

        this.interceptorClass = interceptorClass;
        this.stateManagement = stateManagement;

        assert stateManagement == SINGLETON || stateManagement == PER_METHOD
                : "SINGLETON and PER_METHOD is supported only at the moment";

        if (stateManagement == SINGLETON) {
            singletonInterceptor = createInterceptor();
        }
    }

    public InterceptorAspect(Pointcut pointcut, MethodInterceptor interceptor) {
        this.pointcut = pointcut;
        singletonInterceptor = interceptor;
        stateManagement = SINGLETON;
    }

    private MethodInterceptor createInterceptor() {
        try {
            return (MethodInterceptor) interceptorClass.newInstance();
        } catch (Exception e) {
            throw new AspectException(e);
        }
    }

    public void introduce(AspectInstance aspectInstance) {
    }

    public void adviseMixin(AspectInstance instance, MixinInstance mixin) {
        Method[] methods = getMethodsToAdvise(instance, mixin);
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (stateManagement == SINGLETON) {
                assert singletonInterceptor != null;
                mixin.addInterceptor(method, singletonInterceptor);
            }
            if (stateManagement == PER_METHOD) {
                MethodInterceptor interceptor = createInterceptor();
                mixin.addInterceptor(method, interceptor);
            }
        }
    }

    Method[] getMethodsToAdvise(AspectInstance instance, MixinInstance mixin) {
        return pointcut.methodsToAdvise(instance, mixin);
    }

    public void advise(AspectInstance aspectInstance) {
    }
}
