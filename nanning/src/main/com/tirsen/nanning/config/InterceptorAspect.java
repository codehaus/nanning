package com.tirsen.nanning.config;

import com.tirsen.nanning.AspectException;
import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MethodInterceptor;
import com.tirsen.nanning.MixinInstance;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

public class InterceptorAspect implements Aspect {
    public static final int SINGLETON = 0;
    public static final int PER_METHOD = SINGLETON + 1;
//    public static final int PER_INSTANCE = PER_METHOD + 1;
//    public static final int PER_MIXIN = PER_INSTANCE + 1;

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
        setPointcut(pointcut);

        this.interceptorClass = interceptorClass;
        this.stateManagement = stateManagement;

        assert stateManagement == SINGLETON || stateManagement == PER_METHOD
                : "SINGLETON and PER_METHOD is supported only at the moment";

        if (stateManagement == SINGLETON) {
            singletonInterceptor = createInterceptor();
        }
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

    /**
     * @deprecated override {@link #advise(com.tirsen.nanning.AspectInstance)} instead and manually iterate the mixins.
     */
    public void adviseMixin(AspectInstance instance, MixinInstance mixin) {
    }

    Method[] getMethodsToAdvise(AspectInstance instance, MixinInstance mixin) {
        return pointcut.methodsToAdvise(instance, mixin);
    }

    public void advise(AspectInstance instance) {
        if (pointcut.adviseInstance(instance)) {
            List mixins = instance.getMixins();
            for (Iterator iterator = mixins.iterator(); iterator.hasNext();) {
                MixinInstance mixin = (MixinInstance) iterator.next();
                if (pointcut.adviseMixin(mixin)) {
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
            }
        }
    }

    public Pointcut getPointcut() {
        return pointcut;
    }
}
