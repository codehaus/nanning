package com.tirsen.nanning.config;

import com.tirsen.nanning.MixinInstance;
import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MethodInterceptor;

import java.util.Iterator;
import java.lang.reflect.Method;

public interface Pointcut {
    /**
     * Determine methods to advise for this mixin.
     * @param mixin
     * @return Methods to advise.
     */
    Method[] methodsToAdvise(AspectInstance instance, MixinInstance mixin);

    boolean adviseInstance(AspectInstance instance);

    boolean adviseMixin(MixinInstance mixin);

    boolean adviseMethod(Method method);

    /**
     * Reuses the same interceptor on every advised method
     * @param instance
     * @param interceptor
     */
    void advise(AspectInstance instance, MethodInterceptor interceptor);

    /**
     * Instantiates a new interceptor for each advised method.
     * @param instance
     * @param interceptorClass
     */
    void advise(AspectInstance instance, Class interceptorClass);
}
