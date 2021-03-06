package org.codehaus.nanning.profiler;

import org.codehaus.nanning.config.*;
import org.codehaus.nanning.attribute.*;
import org.codehaus.nanning.profiler.Profiled;
import org.codehaus.nanning.profiler.ProfilerInterceptor;
import org.codehaus.nanning.profiler.ProfilerLogger;

import org.codehaus.nanning.util.OroUtils;

public class ProfilerTest extends AbstractAttributesTest {
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testProfiler() throws Exception {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                AspectSystem aspectSystem = new AspectSystem();
                aspectSystem.addAspect(new FindTargetMixinAspect());
                aspectSystem.addAspect(new InterceptorAspect(P.methodAttribute("profile"), new ProfilerInterceptor()));
                Profiled profiled = (Profiled) aspectSystem.newInstance(Profiled.class);
                ProfilerInterceptor.setMinDuration(0);
                profiled.someMethod();
                profiled.notProfiledMethod();

            }
        });
        thread.start();
        thread.join();
        String log = ProfilerLogger.getProfilerLogger().lastLog;
        assertNotNull(log);
        assertTrue(OroUtils.match(log, "(.*)someMethod: (.*)ms"));
    }

    public void testMinimumProfilingDuration() throws Exception {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                AspectSystem aspectSystem = new AspectSystem();
                aspectSystem.addAspect(new FindTargetMixinAspect());
                aspectSystem.addAspect(new InterceptorAspect(P.methodAttribute("profile"), new ProfilerInterceptor()));
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
