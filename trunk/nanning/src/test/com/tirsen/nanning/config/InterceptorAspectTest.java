package com.tirsen.nanning.config;

import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MixinInstance;
import com.tirsen.nanning.MethodInterceptor;
import com.tirsen.nanning.Invocation;
import junit.framework.TestCase;

import java.lang.reflect.Method;
import java.util.Collections;

public class InterceptorAspectTest extends TestCase {
    private AspectInstance instance;
    private MixinInstance mixin;
    private NOPInterceptor interceptor;
    private Method method;
    private Method method2;

    public static interface Interface {
        void method();

        void method2();
    }

    public static class NOPInterceptor implements MethodInterceptor {
        public Object invoke(Invocation invocation) throws Throwable {
            return null;
        }
    }

    protected void setUp() throws Exception {
        super.setUp();

        instance = new AspectInstance();
        mixin = new MixinInstance(Interface.class, null);
        instance.addMixin(mixin);

        interceptor = new NOPInterceptor();

        method = Interface.class.getMethod("method", null);
        method2 = Interface.class.getMethod("method2", null);
    }

    public void testSingleton() {
        InterceptorAspect interceptorAspect = new InterceptorAspect(interceptor);
        assertEquals(InterceptorAspect.SINGLETON, interceptorAspect.getStateManagement());
        interceptorAspect.advise(instance);
        assertEquals(Collections.singleton(interceptor), mixin.getAllInterceptors());
        assertEquals(Collections.singletonList(interceptor), mixin.getInterceptorsForMethod(method));
        assertEquals(Collections.singletonList(interceptor), mixin.getInterceptorsForMethod(method2));
    }

    public void testPerMethod() {
        InterceptorAspect interceptorAspect = new InterceptorAspect(NOPInterceptor.class, InterceptorAspect.PER_METHOD);
        assertEquals(InterceptorAspect.PER_METHOD, interceptorAspect.getStateManagement());
        interceptorAspect.advise(instance);
        assertEquals(2, mixin.getAllInterceptors().size());
        NOPInterceptor interceptor = (NOPInterceptor) mixin.getInterceptorsForMethod(method).get(0);
        NOPInterceptor interceptor2 = (NOPInterceptor) mixin.getInterceptorsForMethod(method2).get(0);
        assertTrue(interceptor != interceptor2);
    }

    public void testFalsePointcut() {
        InterceptorAspect interceptorAspect = new InterceptorAspect(new AbstractPointcut() {
            protected boolean adviseMethod(Method method) {
                return false;
            }
        }, interceptor);
        assertEquals(0, interceptorAspect.getMethodsToAdvise(instance, mixin).length);
        interceptorAspect.advise(instance);
        assertEquals(0, mixin.getAllInterceptors().size());
    }

    public void testIntroduce() {
        new InterceptorAspect(null).introduce(instance);
        assertEquals(1, instance.getMixins().size());
    }
}
