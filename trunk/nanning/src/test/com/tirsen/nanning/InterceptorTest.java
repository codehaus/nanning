/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import com.tirsen.nanning.definition.AspectClass;
import com.tirsen.nanning.definition.FilterMethodsInterceptor;
import com.tirsen.nanning.definition.InterceptorDefinition;
import com.tirsen.nanning.definition.SingletonInterceptor;
import junit.framework.TestCase;

import java.lang.reflect.Method;

/**
 * TODO document AspectClassTest
 *
 * <!-- $Id: InterceptorTest.java,v 1.3 2003-01-24 13:29:30 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.3 $
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

    public void testStatelessInterceptor() throws NoSuchMethodException {
        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(Intf.class);
        aspectClass.addInterceptor(new InterceptorDefinition(StatelessInterceptorImpl.class));
        aspectClass.addInterceptor(new InterceptorDefinition(MockInterceptor.class));
        aspectClass.setTarget(Impl.class);

        Object proxy = aspectClass.newInstance();
        Interceptor singletonInterceptor = Aspects.getInterceptors(proxy, Intf.class.getMethod("call", null))[0];
        Interceptor interceptor = Aspects.getInterceptors(proxy, Intf.class.getMethod("call", null))[1];
        assertTrue(singletonInterceptor instanceof SingletonInterceptor);

        Object proxy2 = aspectClass.newInstance();
        Interceptor singletonInterceptor2 = Aspects.getInterceptors(proxy2, Intf.class.getMethod("call", null))[0];
        Interceptor interceptor2 = Aspects.getInterceptors(proxy2, Intf.class.getMethod("call", null))[1];
        assertSame("singleton interceptor instantiated twice", singletonInterceptor, singletonInterceptor2);
        assertNotSame("ordinary interceptor not instantiated twice", interceptor, interceptor2);
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
