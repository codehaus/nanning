/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import junit.framework.TestCase;

/**
 * TODO document AspectRepositoryTest
 *
 * <!-- $Id: AspectRepositoryTest.java,v 1.2 2002-11-18 20:56:30 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
 */
public class AspectRepositoryTest extends TestCase
{
    public void testEmpty()
    {
        AspectRepository aspectRepository = new AspectRepository();

        try
        {
            aspectRepository.newInstance(Intf.class);
            ///CLOVER:OFF
            fail("could instantiate aspect before it was configured");
            ///CLOVER:ON
        }
        catch (IllegalArgumentException shouldHappen)
        {
        }
    }

    public void testConfig() throws InstantiationException, IllegalAccessException
    {
        AspectRepository aspectRepository = new AspectRepository();

        // the braces are here to isolate the lexical spaces to ensure that the lexical space is in the repository
        {
            InterceptorDefinition interceptorDefinition = new InterceptorDefinition(MockInterceptor.class);
            aspectRepository.defineInterceptor(interceptorDefinition);

            assertSame(interceptorDefinition, aspectRepository.getInterceptor(MockInterceptor.class));
        }

        {
            InterceptorDefinition interceptorDefinition = new InterceptorDefinition(NullInterceptor.class);
            aspectRepository.defineInterceptor(interceptorDefinition);

            assertSame(interceptorDefinition, aspectRepository.getInterceptor(NullInterceptor.class));
        }

        {
            AspectDefinition aspectDefinition = new AspectDefinition();
            aspectDefinition.setInterface(SideAspect.class);
            aspectDefinition.addInterceptor(aspectRepository.getInterceptor(MockInterceptor.class));
            aspectDefinition.addInterceptor(aspectRepository.getInterceptor(NullInterceptor.class));
            aspectDefinition.setTarget(SideAspectImpl.class);
            aspectRepository.defineAspect(aspectDefinition);

            assertSame(aspectDefinition, aspectRepository.getAspect(SideAspect.class));
        }

        {
            AspectClass aspectClass = new AspectClass();
            aspectClass.setInterface(Intf.class);
            aspectClass.addInterceptor(aspectRepository.getInterceptor(MockInterceptor.class));
            aspectClass.addInterceptor(aspectRepository.getInterceptor(NullInterceptor.class));
            aspectClass.setTarget(Impl.class);
            aspectClass.addSideAspect(aspectRepository.getAspect(SideAspect.class));
            aspectRepository.defineClass(aspectClass);

            assertSame(aspectClass, aspectRepository.getClass(Intf.class));
        }

        Intf intf = (Intf) aspectRepository.newInstance(Intf.class);
        intf.call();
    }

    public void testConfigure() throws NoSuchMethodException, ConfigureException
    {
        AspectRepository aspectRepository = new AspectRepository();
        aspectRepository.configure(AspectRepositoryTest.class.getResource("aspect-repository-test.xml"));

//        AspectRepository aspectRepository = AspectRepository.getInstance();
        Object bigMomma = aspectRepository.newInstance(Intf.class);
        AspectClassTest.verifySideAspect(bigMomma);
    }
}
