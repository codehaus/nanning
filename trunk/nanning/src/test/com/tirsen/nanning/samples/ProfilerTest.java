package com.tirsen.nanning.samples;

import com.tirsen.nanning.config.*;
import com.tirsen.nanning.attribute.*;

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
        assertTrue(log.endsWith("someMethod: 0ms"));


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
