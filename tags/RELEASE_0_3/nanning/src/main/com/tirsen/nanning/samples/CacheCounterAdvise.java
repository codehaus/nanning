package com.tirsen.nanning.samples;

import com.tirsen.nanning.MixinInstance;
import com.tirsen.nanning.MethodInterceptor;
import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.config.Advise;

import java.lang.reflect.Method;

public class CacheCounterAdvise extends Advise {
    int totalCount;
    int missCount;
    private MethodInterceptor cacheInterceptor;

    public CacheCounterAdvise(MethodInterceptor cacheInterceptor) {
        this.cacheInterceptor = cacheInterceptor;
    }

    public void advise(MixinInstance mixinInstance, Method method) {
        mixinInstance.addInterceptor(method, new MethodInterceptor() {
            public Object invoke(Invocation invocation) throws Throwable {
                totalCount++;
                return invocation.invokeNext();
            }
        });
        mixinInstance.addInterceptor(method, cacheInterceptor);
        mixinInstance.addInterceptor(method, new MethodInterceptor() {
            public Object invoke(Invocation invocation) throws Throwable {
                missCount++;
                return invocation.invokeNext();
            }
        });
    }

    public double getCacheHitRatio() {
        return totalCount == 0 ? 1 :  (totalCount - missCount) / (double) totalCount;
    }
}
