package org.codehaus.nanning.profiler;

import org.codehaus.nanning.config.*;
import org.codehaus.nanning.attribute.*;
import org.codehaus.nanning.profiler.Profiled;
import org.codehaus.nanning.profiler.ProfilerInterceptor;
import org.codehaus.nanning.profiler.ProfilerLogger;

public class ProfilerTest extends AbstractAttributesTest {
    public void testProfiler() throws Exception {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                AspectSystem aspectSystem = new AspectSystem();
                aspectSystem.addAspect(new FindTargetMixinAspect());
                aspectSystem.addAspect(new InterceptorAspect(new AttributePointcut("profile"), new ProfilerInterceptor()));
                Profiled profiled = (Profiled) aspectSystem.newInstance(Profiled.class);

                profiled.someMethod();
                profiled.notProfiledMethod();

            }
        });
        thread.start();
        thread.join();
        String log = ProfilerLogger.getProfilerLogger().lastLog;
        assertNotNull(log);
        assertTrue(log.matches("(.*)someMethod: (.*)ms"));
    }

    public void testMinimumProfilingDuration() throws Exception {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                AspectSystem aspectSystem = new AspectSystem();
                aspectSystem.addAspect(new FindTargetMixinAspect());
                aspectSystem.addAspect(new InterceptorAspect(new AttributePointcut("profile"), new ProfilerInterceptor()));
                Profiled profiled = (Profiled) aspectSystem.newInstance(Profiled.class);
                ProfilerInterceptor.setMinDuration(100L);
                profiled.delayTwoHundredMillis();
                profiled.someMethod();

            }
        });
        thread.start();
        thread.join();
        String log = ProfilerLogger.getProfilerLogger().lastLog;
        assertNotNull(log);
        assertTrue(log.indexOf("delayTwoHundredMillis") > 0);

    }
}
