/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.test;

import junit.framework.TestCase;
import com.tirsen.nanning.AspectClass;
import com.tirsen.nanning.InterfaceDefinition;
import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.Interceptor;

/**
 * TODO document AspectClassTest
 *
 * <!-- $Id: AspectClassTest.java,v 1.2 2002-10-23 21:26:43 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
 */
public class AspectClassTest extends TestCase
{
    public void testInterceptor() throws IllegalAccessException, InstantiationException, NoSuchMethodException
    {
        AspectClass aspectClass = AspectClass.create();
        InterfaceDefinition interfaceDefinition = new InterfaceDefinition();
        interfaceDefinition.setInterface(Intf.class);
        interfaceDefinition.addInterceptor(MockAspect.class);
        interfaceDefinition.addInterceptor(MockAspect.class);
        interfaceDefinition.setTarget(Impl.class);
        aspectClass.addInterface(interfaceDefinition);

        Intf intf = (Intf) aspectClass.newInstance();
        Impl impl = (Impl) Aspects.getTarget(intf, Intf.class);

        Interceptor[] interceptors = Aspects.getInterceptors(intf, Intf.class);
        MockAspect aspect = (MockAspect) interceptors[0];
        MockAspect aspect2 = (MockAspect) interceptors[1];

        aspect.expectTarget(impl);
        aspect.expectProxy(intf);
        aspect.expectMethod(Intf.class.getMethod("call", null));
        aspect2.expectTarget(impl);
        aspect2.expectProxy(intf);
        aspect2.expectMethod(Intf.class.getMethod("call", null));

        intf.call();
        impl.verify();
        aspect.verify();
        aspect2.verify();
    }

    public void testAspectsOnProxy() throws InstantiationException, IllegalAccessException
    {
        AspectClass aspectClass = AspectClass.create();
        InterfaceDefinition interfaceDefinition = new InterfaceDefinition();
        interfaceDefinition.setInterface(Intf.class);
        interfaceDefinition.addInterceptor(MockAspect.class);
        interfaceDefinition.setTarget(Impl.class);
        aspectClass.addInterface(interfaceDefinition);
        aspectClass.addInterceptor(MockAspect.class);

        Intf proxy = (Intf) aspectClass.newInstance();

        assertEquals(2, Aspects.getInterceptors(proxy, Intf.class).length);
        MockAspect intfInterceptor = (MockAspect) Aspects.getInterceptors(proxy, Intf.class)[0];
        MockAspect proxyInterceptor = (MockAspect) Aspects.getInterceptors(proxy)[0];

        intfInterceptor.expectCalledTimes(1);
        intfInterceptor.expectProxy(proxy);
        proxyInterceptor.expectProxy(proxy);

        proxy.call();

        intfInterceptor.verify();
        proxyInterceptor.verify();
    }

    public void testSideAspect() throws IllegalAccessException, InstantiationException, NoSuchMethodException
    {
        AspectClass aspectClass = AspectClass.create();
        InterfaceDefinition interfaceDefinition = new InterfaceDefinition();
        interfaceDefinition.setInterface(Intf.class);
        interfaceDefinition.addInterceptor(MockAspect.class);
        interfaceDefinition.addInterceptor(NullAspect.class);
        interfaceDefinition.setTarget(Impl.class);
        aspectClass.addInterface(interfaceDefinition);
        interfaceDefinition = new InterfaceDefinition();
        interfaceDefinition.setInterface(SideAspect.class);
        interfaceDefinition.addInterceptor(NullAspect.class);
        interfaceDefinition.addInterceptor(MockAspect.class);
        interfaceDefinition.setTarget(SideAspectImpl.class);
        aspectClass.addInterface(interfaceDefinition);

        Object bigMomma = aspectClass.newInstance();

        Impl impl = (Impl) Aspects.getTarget(bigMomma, Intf.class);
        MockAspect checkIntf = (MockAspect) (Aspects.getInterceptors(bigMomma, Intf.class)[0]);
        checkIntf.expectProxy(bigMomma);
        checkIntf.expectMethod(Intf.class.getMethod("call", null));
        checkIntf.expectTarget(impl);

        SideAspectImpl sideAspectImpl = (SideAspectImpl) Aspects.getTarget(bigMomma, SideAspect.class);
        MockAspect checkSide = (MockAspect) (Aspects.getInterceptors(bigMomma, SideAspect.class)[1]);
        checkSide.expectProxy(bigMomma);
        checkSide.expectMethod(SideAspect.class.getMethod("sideCall", null));
        checkSide.expectTarget(sideAspectImpl);

        ((Intf) bigMomma).call();
        ((SideAspect) bigMomma).sideCall();

        checkIntf.verify();
        impl.verify();
        checkSide.verify();
        sideAspectImpl.verify();
    }

    public void testNoAspects() throws IllegalAccessException, InstantiationException
    {
        AspectClass aspectClass = AspectClass.create();
        InterfaceDefinition interfaceDefinition = new InterfaceDefinition();
        interfaceDefinition.setInterface(Intf.class);
        interfaceDefinition.setTarget(Impl.class);
        aspectClass.addInterface(interfaceDefinition);
        Intf intf = (Intf) aspectClass.newInstance();

        Impl impl = (Impl) Aspects.getTarget(intf, Intf.class);

        intf.call();
        impl.verify();
    }
}
