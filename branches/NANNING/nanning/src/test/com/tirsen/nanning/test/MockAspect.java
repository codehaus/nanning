/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.test;

import com.tirsen.nanning.Aspect;
import com.tirsen.nanning.AspectContext;
import junit.framework.Assert;

import java.lang.reflect.Method;

/**
 * TODO document MockAspect
 *
 * <!-- $Id: MockAspect.java,v 1.1.1.1 2002-10-20 09:33:53 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1.1.1 $
 */
public class MockAspect implements Aspect
{
    private boolean called;
    private Object realObject;
    private Object actualRealObject;
    private Object proxy;
    private Object actualProxy;

    public MockAspect()
    {
    }

    public void verify()
    {
        Assert.assertTrue("was never called", called);
        Assert.assertSame("real object was not correct during call", realObject, actualRealObject);
        Assert.assertSame("proxy was not correct during call", proxy, actualProxy);
    }

    public Object invoke(Method method, Object[] args, AspectContext context) throws Throwable
    {
        called = true;
        actualRealObject = context.getRealObject();
        actualProxy = context.getProxy();
        return context.invokeNext(method, args, context);
    }

    public void expectRealObject(Object o)
    {
        realObject = o;
    }

    public void expectProxy(Object proxy)
    {
        this.proxy = proxy;
    }
}
