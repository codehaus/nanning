package org.codehaus.nanning.cache;

import org.codehaus.nanning.AspectInstance;
import org.codehaus.nanning.Invocation;
import org.codehaus.nanning.MethodInterceptor;
import org.codehaus.nanning.config.Aspect;
import org.codehaus.nanning.config.P;
import org.codehaus.nanning.config.Pointcut;

public class CacheAspect implements Aspect {
    int totalCount;
    int missCount;
    private Class cacheInterceptor;
    private Pointcut pointcut = P.methodAttribute("cache");

    public CacheAspect(Class cacheInterceptor) {
        this.cacheInterceptor = cacheInterceptor;
    }

    public CacheAspect() {
        this(CacheInterceptor.class);
    }

    public void introduce(AspectInstance aspectInstance) {
    }

    public void advise(AspectInstance instance) {
        pointcut.advise(instance, new MethodInterceptor() {
            public Object invoke(Invocation invocation) throws Throwable {
                totalCount++;
                return invocation.invokeNext();
            }
        });
        pointcut.advise(instance, cacheInterceptor);
        pointcut.advise(instance, new MethodInterceptor() {
            public Object invoke(Invocation invocation) throws Throwable {
                missCount++;
                return invocation.invokeNext();
            }
        });
    }

    public double getCacheHitRatio() {
        return totalCount == 0 ? 1 : (totalCount - missCount) / (double) totalCount;
    }
}
