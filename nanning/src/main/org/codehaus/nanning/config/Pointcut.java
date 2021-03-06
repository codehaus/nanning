package org.codehaus.nanning.config;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import org.codehaus.nanning.*;

public abstract class Pointcut {

    /**
     * Reuses the same interceptor on every advised method
     * @param instance
     * @param interceptor
     */
    public void advise(AspectInstance instance, MethodInterceptor interceptor) {
        advise(instance, interceptor, null);
    }

    /**
     * Instantiates a new interceptor for each advised method.
     * @param instance
     * @param interceptorClass
     */
    public void advise(AspectInstance instance, Class interceptorClass) {
        advise(instance, null, interceptorClass);
    }

    public void advise(AspectInstance instance, MethodInterceptor interceptor, Class interceptorClass) {
        List mixins = instance.getMixins();
        for (Iterator iterator = mixins.iterator(); iterator.hasNext();) {
            Mixin mixin = (Mixin) iterator.next();
            Method[] methods = mixin.getAllMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                if (adviseMethod(instance, mixin, method)) {
                    if (interceptor != null) {
                        mixin.addInterceptor(method, interceptor);
                    } else if (interceptorClass != null) {
                        try {
                            mixin.addInterceptor(method, (MethodInterceptor) interceptorClass.newInstance());
                        } catch (Exception e) {
                            throw new AspectException(e);
                        }
                    } else {
                        throw new AssertionException("interceptor or class needs to be specified");
                    }
                }
            }
        }
    }

    /**
     * Override this method for a pointcut that selects methods to advise.
     *
     * @param instance
     * @param mixin
     * @param method
     * @return
     */
    public boolean adviseMethod(AspectInstance instance, Mixin mixin, Method method) {
        return false;
    }

    /**
     * Override this method for a pointcut that selects aspect-instances to introduce on.
     *
     * @param instance
     * @return
     */
    public boolean introduceOn(AspectInstance instance) {
        return false;
    }

    public void introduce(AspectInstance instance, Mixin mixin) {
        if (introduceOn(instance)) {
            instance.addMixin(mixin);
        }
    }
}
