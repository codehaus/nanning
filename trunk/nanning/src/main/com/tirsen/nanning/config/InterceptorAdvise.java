package com.tirsen.nanning.config;

import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MixinInstance;
import com.tirsen.nanning.Interceptor;
import com.tirsen.nanning.MethodInterceptor;

import java.lang.reflect.Method;

public class InterceptorAdvise extends Advise {
    public static final int SINGLETON = 0;
    public static final int PER_INSTANCE = SINGLETON + 1;
    public static final int PER_MIXIN = PER_INSTANCE + 1;
    public static final int PER_METHOD = PER_MIXIN + 1;

    private Class interceptorClass;
    private int stateManagement;
    private MethodInterceptor singletonInterceptor;

    public InterceptorAdvise(MethodInterceptor interceptor) {
        singletonInterceptor = interceptor;
        stateManagement = SINGLETON;
    }

    public InterceptorAdvise(Class interceptorClass, int stateManagement) {
        this.interceptorClass = interceptorClass;

        this.stateManagement = stateManagement;

        assert stateManagement == SINGLETON || stateManagement == PER_METHOD
                : "SINGLETON and PER_METHOD is supported only at the moment";

        if (stateManagement == SINGLETON) {
            singletonInterceptor = createInterceptor();
        }
    }

    private MethodInterceptor createInterceptor() {
        try {
            return (MethodInterceptor) interceptorClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void advise(MixinInstance mixinInstance, Method method) {
        if (stateManagement == SINGLETON) {
            assert singletonInterceptor != null;
            mixinInstance.addInterceptor(method, singletonInterceptor);
        }
        if (stateManagement == PER_METHOD) {
            mixinInstance.addInterceptor(method, createInterceptor());
        }
    }
}
