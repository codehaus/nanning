/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.test;

import com.tirsen.nanning.Interceptor;
import com.tirsen.nanning.Invocation;
import junit.framework.Assert;

import java.lang.reflect.Method;

/**
 * TODO document MockAspect
 *
 * <!-- $Id: MockAspect.java,v 1.3 2002-10-22 18:28:09 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.3 $
 */
public class MockAspect implements Interceptor
{
    private boolean called;
    private Object expectTarget;
    private Object actualTarget;
    private Object expectProxy;
    private Object actualProxy;
    private Method expectMethod;

    public MockAspect()
    {
    }

    public void verify()
    {
        Assert.assertTrue("was never called", called);
        Assert.assertSame("real object was not correct during sideCall", expectTarget, actualTarget);
        Assert.assertSame("expectProxy was not correct during sideCall", expectProxy, actualProxy);
    }

    public Object invoke(Invocation invocation) throws Throwable
    {
        called = true;
        actualTarget = invocation.getTarget();
        actualProxy = invocation.getProxy();
        
        int index = invocation.getCurrentIndex();
        Assert.assertSame(this, invocation.getInterceptor(invocation.getCurrentIndex()));

        // check that getNumberOfAspects is correct
        int numberOfAspects = invocation.getNumberOfAspects();
        invocation.getInterceptor(numberOfAspects - 1); // should work...
        try
        {
            invocation.getInterceptor(numberOfAspects); // should not work...
            ///CLOVER:OFF
            Assert.fail("Invocation.getNumberOfAspects doesn't work.");
            ///CLOVER:ON
        }
        catch (Exception shouldHappen)
        {
        }

        Assert.assertEquals(expectMethod, invocation.getMethod());

        Assert.assertNull(invocation.getArgs());

        return invocation.invokeNext(invocation);
    }

    public void expectTarget(Object o)
    {
        expectTarget = o;
    }

    public void expectMethod(Method expectMethod)
    {
        this.expectMethod = expectMethod;
    }

    public void expectProxy(Object proxy)
    {
        this.expectProxy = proxy;
    }
}
