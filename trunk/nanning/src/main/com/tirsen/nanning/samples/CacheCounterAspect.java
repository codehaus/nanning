package com.tirsen.nanning.samples;

import com.tirsen.nanning.MixinInstance;
import com.tirsen.nanning.MethodInterceptor;
import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.attribute.Attributes;
import com.tirsen.nanning.config.Aspect;

import java.lang.reflect.Method;

public class CacheCounterAspect implements Aspect {
    int totalCount;
    int missCount;
    private MethodInterceptor cacheInterceptor;

    public CacheCounterAspect(MethodInterceptor cacheInterceptor) {
        this.cacheInterceptor = cacheInterceptor;
    }

    public void introduce(AspectInstance aspectInstance) {
    }

    public void advise(AspectInstance aspectInstance) {
    }

    public void adviseMixin(AspectInstance aspectInstance, MixinInstance mixin) {
        Method[] methods = mixin.getAllMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (adviseMethod(method)) {
                mixin.addInterceptor(method, new MethodInterceptor() {
                    public Object invoke(Invocation invocation) throws Throwable {
                        totalCount++;
                        return invocation.invokeNext();
                    }
                });
                mixin.addInterceptor(method, cacheInterceptor);
                mixin.addInterceptor(method, new MethodInterceptor() {
                    public Object invoke(Invocation invocation) throws Throwable {
                        missCount++;
                        return invocation.invokeNext();
                    }
                });
            }
        }
    }

    private boolean adviseMethod(Method method) {
        return Attributes.hasAttribute(method, "cache");
    }

    public double getCacheHitRatio() {
        return totalCount == 0 ? 1 :  (totalCount - missCount) / (double) totalCount;
    }
}
