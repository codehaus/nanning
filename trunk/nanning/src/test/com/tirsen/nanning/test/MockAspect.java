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

/**
 * TODO document MockAspect
 *
 * <!-- $Id: MockAspect.java,v 1.2 2002-10-21 21:07:31 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
 */
public class MockAspect implements Interceptor
{
    private boolean called;
    private Object target;
    private Object actualTarget;
    private Object proxy;
    private Object actualProxy;

    public MockAspect()
    {
    }

    public void verify()
    {
        Assert.assertTrue("was never called", called);
        if (target != null)
        {
            Assert.assertSame("real object was not correct during call", target, actualTarget);
        }
        if (proxy != null)
        {
            Assert.assertSame("proxy was not correct during call", proxy, actualProxy);
        }
    }

    public Object invoke(Invocation invocation) throws Throwable
    {
        called = true;
        actualTarget = invocation.getTarget();
        actualProxy = invocation.getProxy();
        
        int index = invocation.getCurrentIndex();
        Assert.assertSame(this, invocation.getAspect(invocation.getCurrentIndex()));

        // check that getNumberOfAspects is correct
        int numberOfAspects = invocation.getNumberOfAspects();
        invocation.getAspect(numberOfAspects - 1); // should work...
        try
        {
            invocation.getAspect(numberOfAspects); // should not work...
            ///CLOVER:OFF
            Assert.fail("Invocation.getNumberOfAspects doesn't work.");
            ///CLOVER:ON
        }
        catch (Exception shouldHappen)
        {
        }

        Assert.assertEquals(Intf.class.getMethod("call", null), invocation.getMethod());

        Assert.assertNull(invocation.getArgs());

        return invocation.invokeNext(invocation);
    }

    public void expectTarget(Object o)
    {
        target = o;
    }

    public void expectProxy(Object proxy)
    {
        this.proxy = proxy;
    }
}
