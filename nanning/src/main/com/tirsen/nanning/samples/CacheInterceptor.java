package com.tirsen.nanning.samples;

import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.attribute.Attributes;
import com.tirsen.nanning.definition.FilterMethodsInterceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CacheInterceptor implements FilterMethodsInterceptor {
    private static final Log logger = LogFactory.getLog(CacheInterceptor.class);

    /**
     * TODO Should be an LRU-cache
     */
    private Map cache = new HashMap();

    public boolean interceptsMethod(Method method) {
        return Attributes.hasAttribute(method, "cache");
    }

    public Object invoke(Invocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        Map methodCache = getMethodCache(method);
        List args = Arrays.asList(invocation.getArgs()); // needs args as collection for hashCode and equals
        Object cachedResult = methodCache.get(args);
        if(cachedResult == null) {
            logger.debug("caching call for method " + method + " with args " + args);
            cachedResult = invocation.invokeNext();
            methodCache.put(args, cachedResult);
        }
        return cachedResult;
    }

    private Map getMethodCache(Method method) {
        Map methodCache = (Map) cache.get(method);
        if(methodCache == null) {
            methodCache = new HashMap();
            cache.put(method, methodCache);
        }
        return methodCache;
    }

    public void clearCache() {
        cache.clear();
    }
}
