/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.codehaus.nanning.definition;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.List;

import org.codehaus.nanning.definition.AspectClass;
import org.codehaus.nanning.definition.InterceptorDefinition;
import org.codehaus.nanning.definition.SingletonInterceptor;
import org.codehaus.nanning.*;
import junit.framework.TestCase;

/**
 * TODO document AspectClassTest
 *
 * <!-- $Id: InterceptorTest.java,v 1.2 2003-07-12 16:48:16 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.2 $
 */
public class InterceptorTest extends TestCase {
    public void testInterceptor() throws IllegalAccessException, InstantiationException, NoSuchMethodException {
        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(Intf.class);
        aspectClass.addInterceptor(MockInterceptor.class);
        aspectClass.addInterceptor(MockInterceptor.class);
        aspectClass.setTarget(IntfImpl.class);

        Intf intf = (Intf) aspectClass.newInstance();
        IntfImpl impl = (IntfImpl) Aspects.getTarget(intf, Intf.class);

        Set interceptorsSet = Aspects.getAspectInstance(intf).getMixinForInterface(Intf.class).getAllInterceptors();
        Interceptor[] interceptors = (Interceptor[]) interceptorsSet.toArray(new Interceptor[interceptorsSet.size()]);
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

    public static interface ErrorIntf {
        void call() throws Exception;
    }

    public static class StatelessInterceptorImpl implements MethodInterceptor, SingletonInterceptor {
        public Object invoke(Invocation invocation) throws Throwable {
            return invocation.invokeNext();
        }
    }

    public void testStatelessInterceptor() throws NoSuchMethodException {
        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(Intf.class);
        aspectClass.addInterceptor(new InterceptorDefinition(StatelessInterceptorImpl.class));
        aspectClass.addInterceptor(new InterceptorDefinition(MockInterceptor.class));
        aspectClass.setTarget(IntfImpl.class);

        Object proxy = aspectClass.newInstance();
        List interceptors = Aspects.getAspectInstance(proxy).getInterceptorsForMethod(Intf.class.getMethod("call", null));
        Interceptor singletonInterceptor = ((MethodInterceptor[]) interceptors.toArray(new MethodInterceptor[interceptors.size()]))[0];
        List interceptors1 = Aspects.getAspectInstance(proxy).getInterceptorsForMethod(Intf.class.getMethod("call", null));
        Interceptor interceptor = ((MethodInterceptor[]) interceptors1.toArray(new MethodInterceptor[interceptors1.size()]))[1];
        assertTrue(singletonInterceptor instanceof SingletonInterceptor);

        Object proxy2 = aspectClass.newInstance();
        List interceptors2 = Aspects.getAspectInstance(proxy2).getInterceptorsForMethod(Intf.class.getMethod("call", null));
        Interceptor singletonInterceptor2 = ((MethodInterceptor[]) interceptors2.toArray(new MethodInterceptor[interceptors2.size()]))[0];
        List interceptors3 = Aspects.getAspectInstance(proxy2).getInterceptorsForMethod(Intf.class.getMethod("call", null));
        Interceptor interceptor2 = ((MethodInterceptor[]) interceptors3.toArray(new MethodInterceptor[interceptors3.size()]))[1];
        assertSame("singleton interceptor instantiated twice", singletonInterceptor, singletonInterceptor2);
        assertNotSame("ordinary interceptor not instantiated twice", interceptor, interceptor2);
    }

    public static class TestFilterMethodsInterceptor implements FilterMethodsInterceptor {
        public boolean interceptsMethod(Method method) {
            return method.getName().equals("interceptThis");
        }

        public Object invoke(Invocation invocation) throws Throwable {
            if (!invocation.getMethod().getName().equals("interceptThis")) {
                fail("should not intercept " + invocation.getMethod());
            }
            return invocation.invokeNext();
        }
    }

    public static interface TestFilterIntf {
        void interceptThis();

        void dontInterceptThis();
    }

    public static class TestFilterImpl implements TestFilterIntf {
        public void interceptThis() {
        }

        public void dontInterceptThis() {
        }
    }

    public void testFilterMethods() {
        AspectClass aspectClass = new AspectClass();
        aspectClass.setInterface(TestFilterIntf.class);
        aspectClass.addInterceptor(TestFilterMethodsInterceptor.class);
        aspectClass.setTarget(TestFilterImpl.class);
        TestFilterIntf instance = (TestFilterIntf) aspectClass.newInstance();
        instance.interceptThis();
        instance.dontInterceptThis();
    }
}
