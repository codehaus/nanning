/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.test;

import junit.framework.TestCase;
import com.tirsen.nanning.AspectClass;
import com.tirsen.nanning.AspectDefinition;
import com.tirsen.nanning.AspectRepository;
import com.tirsen.nanning.InterceptorDefinition;

/**
 * TODO document AspectRepositoryTest
 *
 * <!-- $Id: AspectRepositoryTest.java,v 1.1 2002-10-27 12:36:41 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public class AspectRepositoryTest extends TestCase
{
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
}
