/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.test;

import junit.framework.TestCase;
import com.tirsen.nanning.AspectProxy;

/**
 * TODO document AspectProxyTest
 *
 * <!-- $Id: AspectProxyTest.java,v 1.2 2002-10-21 21:07:31 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
 */
public class AspectProxyTest extends TestCase
{
    public void testAspectProxy()
    {
        Impl impl = new Impl();

        AspectProxy aspectProxy = AspectProxy.create(impl);

        MockAspect aspect = new MockAspect();
        aspectProxy.addAspect(aspect);
        MockAspect aspect2 = new MockAspect();
        aspectProxy.addAspect(aspect2);

        Intf intf = (Intf) aspectProxy.createProxy(new Class[] { Intf.class });

        aspect.expectTarget(impl);
        aspect.expectProxy(intf);
        aspect2.expectTarget(impl);
        aspect2.expectProxy(intf);

        intf.call();
        impl.verify();
        aspect.verify();
        aspect2.verify();
    }

    public void testNoAspects()
    {
        Impl impl = new Impl();
        AspectProxy aspectProxy = AspectProxy.create(impl);
        Intf intf = (Intf) aspectProxy.createProxy(new Class[] { Intf.class });
        intf.call();
        impl.verify();
    }
}
