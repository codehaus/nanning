package org.codehaus.nanning.profiler;

import org.codehaus.nanning.Invocation;
import org.codehaus.nanning.MethodInterceptor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class ProfilerInterceptor implements MethodInterceptor {
    static ThreadLocal threadLocal = new ThreadLocal();

    private static long minDuration = 0;

    public Object invoke(Invocation invocation) throws Throwable {

        Map methodsStartingTime = (Map) threadLocal.get();
        if (methodsStartingTime == null) {
            methodsStartingTime = new HashMap();
            threadLocal.set(methodsStartingTime);
        }
        Method method = invocation.getMethod();
        methodsStartingTime.put(method, new Long(System.currentTimeMillis()));
        Object result = invocation.invokeNext();
        long duration = System.currentTimeMillis() - ((Long) methodsStartingTime.get(method)).longValue();
        if (duration >= minDuration) ProfilerLogger.getProfilerLogger().log(invocation, duration);
        return result;
    }

    public static long getMinDuration() {
        return minDuration;
    }

    public static void setMinDuration(long l) {
        minDuration = l;
    }

}
