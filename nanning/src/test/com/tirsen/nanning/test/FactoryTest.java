/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.test;

import com.tirsen.nanning.Factory;
import com.tirsen.nanning.Aspects;
import junit.framework.TestCase;

/**
 * TODO document FactoryTest
 *
 * <!-- $Id: FactoryTest.java,v 1.1 2002-10-21 21:07:31 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public class FactoryTest extends TestCase
{
    public void testFactory() throws InstantiationException, IllegalAccessException
    {
        Factory factory = Factory.addFactory(Intf.class);
        assertSame(factory, Factory.getFactory(Intf.class));

        factory = Factory.getFactory(Intf.class);
        factory.addAspect(MockAspect.class);
        factory.setDefaultTarget(Impl.class);

        Intf intf = (Intf) factory.newInstance();
        MockAspect mockAspect = (MockAspect) Aspects.getAspects(intf)[0];
        Impl target = (Impl) Aspects.getTarget(intf);

        intf.call();
        mockAspect.verify();
        target.verify();
    }
}
