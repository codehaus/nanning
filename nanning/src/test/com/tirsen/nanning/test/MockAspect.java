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
 * <!-- $Id: MockAspect.java,v 1.5 2002-10-23 21:26:43 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.5 $
 */
public class MockAspect implements Interceptor
{
    private Object expectTarget;
    private Object actualTarget;
    private Object expectProxy;
    private Object actualProxy;
    private Method expectMethod;
    private int expectCalledTimes = -1;
    private int calledTimes;

    public MockAspect()
    {
    }

    public void verify()
    {
        Assert.assertTrue("was never called", calledTimes != 0);
        if (expectCalledTimes != -1)
        {
            Assert.assertEquals("was not called correct number of times", expectCalledTimes, calledTimes);
        }
        if (expectTarget != null)
        {
            Assert.assertSame("real object was not correct during sideCall", expectTarget, actualTarget);
        }
        Assert.assertSame("expectProxy was not correct during sideCall", expectProxy, actualProxy);
    }

    public Object invoke(Invocation invocation) throws Throwable
    {
        calledTimes++;
        actualTarget = invocation.getTarget();
        actualProxy = invocation.getProxy();

        int index = invocation.getCurrentIndex();
        Assert.assertSame(this, invocation.getInterceptor(invocation.getCurrentIndex()));

        // check that getNumberOfInterceptors is correct
        int numberOfAspects = invocation.getNumberOfInterceptors();
        invocation.getInterceptor(numberOfAspects - 1); // should work...
        try
        {
            invocation.getInterceptor(numberOfAspects); // should not work...
            ///CLOVER:OFF
            Assert.fail("Invocation.getNumberOfInterceptors doesn't work.");
            ///CLOVER:ON
        }
        catch (Exception shouldHappen)
        {
        }

        if (expectMethod != null)
        {
            Assert.assertEquals(expectMethod, invocation.getMethod());
        }

        Assert.assertNull(invocation.getArgs());

        return invocation.invokeNext();
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

    public void expectCalledTimes(int i)
    {
        this.expectCalledTimes = i;
    }
}
