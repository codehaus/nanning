package org.codehaus.nanning;

import java.lang.reflect.Method;

import junit.framework.TestCase;

public class MethodInterceptorMappingTest extends TestCase {
    private MethodInterceptorMapping interceptorMapping;
    private Method addMethod;
    private Method getMethod;
    private Interceptor interceptor1;
    private Interceptor interceptor2;

    protected void setUp() throws Exception {
        super.setUp();
        interceptorMapping = new MethodInterceptorMapping();
        addMethod = MethodInterceptorMapping.class.getMethod("add", new Class[]{Method.class, Interceptor.class});
        getMethod = MethodInterceptorMapping.class.getMethod("get", new Class[]{Method.class});
        interceptor1 = new Interceptor() {};
        interceptor2 = new Interceptor() {};
    }

    public void testInitialInterceptorMappingValue() throws Exception {
        assertEquals(0, interceptorMapping.getAllInterceptors().size());
    }

    public void testGettingMethodFromInterceptorMapping() throws Exception {
        interceptorMapping.add(addMethod, interceptor1);
        interceptorMapping.add(addMethod, interceptor2);
        assertEquals(2, interceptorMapping.get(addMethod).size());
        assertSame(interceptor1, interceptorMapping.get(addMethod).get(0));
        assertSame(interceptor2, interceptorMapping.get(addMethod).get(1));
    }

    public void testGettingAllInterceptorsForAllMethodsHasSetSemantics() throws Exception {
        interceptorMapping.add(addMethod, interceptor1);
        interceptorMapping.add(getMethod, interceptor1);
        assertEquals(1, interceptorMapping.getAllInterceptors().size());
        assertTrue(interceptorMapping.getAllInterceptors().contains(interceptor1));
    }

    public void testGettingAllInterceptorsForAllMethods() throws Exception {
        interceptorMapping.add(addMethod, interceptor1);
        interceptorMapping.add(getMethod, interceptor2);
        assertEquals(2, interceptorMapping.getAllInterceptors().size());
        assertTrue(interceptorMapping.getAllInterceptors().contains(interceptor1));
        assertTrue(interceptorMapping.getAllInterceptors().contains(interceptor2));
    }
}
