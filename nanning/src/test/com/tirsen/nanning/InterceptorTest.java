/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import junit.framework.TestCase;

import java.lang.reflect.Method;

/**
 * TODO document AspectClassTest
 *
 * <!-- $Id: InterceptorTest.java,v 1.1 2002-12-11 10:57:52 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.1 $
 */
public class InterceptorTest extends TestCase
{
    public void testInterceptor() throws IllegalAccessException, InstantiationException, NoSuchMethodException
    {
        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(Intf.class);
        aspectClass.addInterceptor(MockInterceptor.class);
        aspectClass.addInterceptor(MockInterceptor.class);
        aspectClass.setTarget(Impl.class);

        Intf intf = (Intf) aspectClass.newInstance();
        Impl impl = (Impl) Aspects.getTarget(intf, Intf.class);

        Interceptor[] interceptors = Aspects.getInterceptors(intf, Intf.class);
        MockInterceptor interceptor = (MockInterceptor) interceptors[0];
        MockInterceptor interceptor2 = (MockInterceptor) interceptors[1];

        interceptor.expectTarget(impl);
        interceptor.expectProxy(intf);
        interceptor.expectMethod(Intf.class.getMethod("call", null));
        interceptor2.expectTarget(impl);
        interceptor2.expectProxy(intf);
        interceptor2.expectMethod(Intf.class.getMethod("call", null));

        intf.call();
        impl.verify();
        interceptor.verify();
        interceptor2.verify();
    }

    public static interface ErrorIntf
    {
        void call() throws Exception;
    }

    public static class StatelessInterceptorImpl implements MethodInterceptor, SingletonInterceptor
    {
        public Object invoke(Invocation invocation) throws Throwable
        {
            return invocation.invokeNext();
        }
    }

    public void testStatelessInterceptor()
    {
        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(Intf.class);
        aspectClass.addInterceptor(new InterceptorDefinition(StatelessInterceptorImpl.class));
        aspectClass.addInterceptor(new InterceptorDefinition(MockInterceptor.class));
        aspectClass.setTarget(Impl.class);

        Object proxy = aspectClass.newInstance();
        Interceptor statelessInterceptor = Aspects.getInterceptors(proxy)[0];
        Interceptor interceptor = Aspects.getInterceptors(proxy)[1];
        assertTrue(statelessInterceptor instanceof SingletonInterceptor);

        Object proxy2 = aspectClass.newInstance();
        Interceptor statelessInterceptor2 = Aspects.getInterceptors(proxy2)[0];
        Interceptor interceptor2 = Aspects.getInterceptors(proxy2)[1];
        assertSame(statelessInterceptor, statelessInterceptor2);
        assertNotSame(interceptor, interceptor2);
    }

    public static class TestFilterMethodsInterceptor implements FilterMethodsInterceptor
    {
        public boolean interceptsMethod(Method method)
        {
            return method.getName().equals("interceptThis");
        }

        public Object invoke(Invocation invocation) throws Throwable
        {
            if(!invocation.getMethod().getName().equals("interceptThis"))
            {
                fail("should not intercept " + invocation.getMethod());
            }
            return invocation.invokeNext();
        }
    }

    public static interface TestFilterIntf
    {
        void interceptThis();
        void dontInterceptThis();
    }

    public static class TestFilterImpl implements TestFilterIntf
    {
        public void interceptThis()
        {
        }

        public void dontInterceptThis()
        {
        }
    }

    public void testFilterMethods()
    {
        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(TestFilterIntf.class);
        aspectClass.addInterceptor(TestFilterMethodsInterceptor.class);
        aspectClass.setTarget(TestFilterImpl.class);
        TestFilterIntf instance = (TestFilterIntf) aspectClass.newInstance();
        instance.interceptThis();
        instance.dontInterceptThis();
    }
}
