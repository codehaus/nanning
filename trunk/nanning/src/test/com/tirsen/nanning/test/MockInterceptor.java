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
 * TODO document MockInterceptor
 *
 * <!-- $Id: MockInterceptor.java,v 1.2 2002-10-28 18:51:01 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
 */
public class MockInterceptor implements Interceptor
{
    private Object expectTarget;
    private Object actualTarget;
    private Object expectProxy;
    private Object actualProxy;
    private Method expectMethod;
    private int expectCalledTimes = -1;
    private int calledTimes;
    private int expectAtIndex = -1;
    private int actualAtIndex;
    private int expectNumberOfInterceptors = -1;
    private int actualNumberOfInterceptors;
    private String name;

    public MockInterceptor()
    {
    }

    public void verify()
    {
        Assert.assertTrue("was never called", calledTimes != 0);
        if (expectCalledTimes != -1)
        {
            Assert.assertEquals("was not called correct number of times", expectCalledTimes, calledTimes);
        }
        Assert.assertSame("expectProxy was not correct during call", expectProxy, actualProxy);
    }

    public Object invoke(Invocation invocation) throws Throwable
    {
        calledTimes++;
        actualTarget = invocation.getTarget();
        actualProxy = invocation.getProxy();

        actualAtIndex = invocation.getCurrentIndex();

        Assert.assertSame(this, invocation.getInterceptor(invocation.getCurrentIndex()));

        // check that getNumberOfInterceptors is correct
        actualNumberOfInterceptors = invocation.getNumberOfInterceptors();
        invocation.getInterceptor(actualNumberOfInterceptors - 1); // should work...
        try
        {
            invocation.getInterceptor(actualNumberOfInterceptors); // should not work...
            ///CLOVER:OFF
            Assert.fail("Invocation.getNumberOfInterceptors doesn't work.");
            ///CLOVER:ON
        }
        catch (Exception shouldHappen)
        {
        }

        if (expectTarget != null)
        {
            Assert.assertSame("real object was not correct during sideCall", expectTarget, actualTarget);
        }
        if (expectMethod != null)
        {
            Assert.assertEquals(expectMethod, invocation.getMethod());
        }

        Assert.assertNull(invocation.getArgs());

        if (expectAtIndex != -1)
        {
            Assert.assertEquals("interceptor not at correct index during call",
                    expectAtIndex, actualAtIndex);
        }
        if (expectNumberOfInterceptors != -1)
        {
            Assert.assertEquals("number of interceptor not at correct index during call",
                    expectNumberOfInterceptors, actualNumberOfInterceptors);
        }

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

    /**
     * What index should this interceptor be at when called.
     * @param index
     */
    public void expectAtIndex(int index)
    {
        this.expectAtIndex = index;
    }

    public void expectNumberOfInterceptors(int index)
    {
        this.expectNumberOfInterceptors = index;
    }
}
