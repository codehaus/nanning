package org.codehaus.nanning.config;

import org.codehaus.nanning.AspectInstance;
import org.codehaus.nanning.Mixin;
import org.codehaus.nanning.MethodInterceptor;
import org.codehaus.nanning.Invocation;
import junit.framework.TestCase;

import java.lang.reflect.Method;
import java.util.Collections;

public class InterceptorAspectTest extends TestCase {
    private AspectInstance instance;
    private Mixin mixin;
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
        mixin = new Mixin(Interface.class, null);
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

    public void testPerInstance() {
        AspectInstance instance2 = new AspectInstance();
        instance2.addMixin(new Mixin(Interface.class, null));

        InterceptorAspect interceptorAspect = new InterceptorAspect(NOPInterceptor.class, InterceptorAspect.PER_INSTANCE);
        assertEquals(InterceptorAspect.PER_INSTANCE, interceptorAspect.getStateManagement());

        interceptorAspect.advise(instance);
        interceptorAspect.advise(instance2);

        assertEquals(1, instance.getAllInterceptors().size());
        assertEquals(1, instance2.getAllInterceptors().size());
        NOPInterceptor interceptor = (NOPInterceptor) instance.getInterceptorsForMethod(method).get(0);
        NOPInterceptor interceptor2 = (NOPInterceptor) instance2.getInterceptorsForMethod(method2).get(0);
        assertTrue(interceptor != interceptor2);
    }

    public void testFalsePointcut() {
        InterceptorAspect interceptorAspect = new InterceptorAspect(new Pointcut() {
            public boolean adviseMethod(AspectInstance instance, Mixin mixin, Method method) {
                return false;
            }
        }, interceptor);
        interceptorAspect.advise(instance);
        assertEquals(0, mixin.getAllInterceptors().size());
    }

    public void testIntroduce() {
        new InterceptorAspect(null).introduce(instance);
        assertEquals(1, instance.getMixins().size());
    }
}
