package com.tirsen.nanning.config;

import com.tirsen.nanning.MixinInstance;
import com.tirsen.nanning.AspectInstance;

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
}
