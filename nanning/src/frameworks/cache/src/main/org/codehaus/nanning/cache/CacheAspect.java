package org.codehaus.nanning.cache;

import org.codehaus.nanning.AspectInstance;
import org.codehaus.nanning.Invocation;
import org.codehaus.nanning.MethodInterceptor;
import org.codehaus.nanning.MixinInstance;
import org.codehaus.nanning.config.Aspect;
import org.codehaus.nanning.config.AttributePointcut;
import org.codehaus.nanning.config.Pointcut;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

public class CacheAspect implements Aspect {
    int totalCount;
    int missCount;
    private MethodInterceptor cacheInterceptor;
    private Pointcut pointcut = new AttributePointcut("cache");

    public CacheAspect(MethodInterceptor cacheInterceptor) {
        this.cacheInterceptor = cacheInterceptor;
    }

    public CacheAspect() {
        this(new CacheInterceptor());
    }

    public void introduce(AspectInstance aspectInstance) {
    }

    public void advise(AspectInstance instance) {
        List mixins = instance.getMixins();
        for (Iterator iterator = mixins.iterator(); iterator.hasNext();) {
            MixinInstance mixin = (MixinInstance) iterator.next();
            Method[] methods = pointcut.methodsToAdvise(instance, mixin);
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
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

    public double getCacheHitRatio() {
        return totalCount == 0 ? 1 :  (totalCount - missCount) / (double) totalCount;
    }
}
