/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.codehaus.nanning.definition;

import junit.framework.TestCase;
import org.codehaus.nanning.AspectFactory;

/**
 * TODO document AspectRepositoryTest
 *
 * <!-- $Id: AspectRepositoryTest.java,v 1.1 2003-07-04 10:53:57 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.1 $
 */
public class AspectRepositoryTest extends TestCase {
    public void testEmpty() {
        AspectFactory aspectRepository = new AspectRepository();

        try {
            aspectRepository.newInstance(Intf.class);
            ///CLOVER:OFF
            fail("could instantiate aspect before it was configured");
            ///CLOVER:ON
        } catch (IllegalArgumentException shouldHappen) {
        }
    }

    public void testConfig() throws InstantiationException, IllegalAccessException {
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
            aspectDefinition.setInterface(TestMixin.class);
            aspectDefinition.addInterceptor(aspectRepository.getInterceptor(MockInterceptor.class));
            aspectDefinition.addInterceptor(aspectRepository.getInterceptor(NullInterceptor.class));
            aspectDefinition.setTarget(TestMixinImpl.class);
            aspectRepository.defineAspect(aspectDefinition);

            assertSame(aspectDefinition, aspectRepository.getAspect(TestMixin.class));
        }

        {
            AspectClass aspectClass = new AspectClass();
            aspectClass.setInterface(Intf.class);
            aspectClass.addInterceptor(aspectRepository.getInterceptor(MockInterceptor.class));
            aspectClass.addInterceptor(aspectRepository.getInterceptor(NullInterceptor.class));
            aspectClass.setTarget(IntfImpl.class);
            aspectClass.addAspect(aspectRepository.getAspect(TestMixin.class));
            aspectRepository.defineClass(aspectClass);

            assertSame(aspectClass, aspectRepository.getClass(Intf.class));
        }

        Intf intf = (Intf) aspectRepository.newInstance(Intf.class);
        intf.call();
    }

    public void testConfigure() throws NoSuchMethodException, ConfigureException {
        AspectRepository aspectRepository = new AspectRepository();
        java.net.URL resource = AspectRepositoryTest.class.getResource("aspect-repository-test.xml");
        aspectRepository.configure(resource);

        //        AspectRepository aspectRepository = AspectRepository.getInstance();
        Object bigMomma = aspectRepository.newInstance(Intf.class);
        AspectClassTest.verifySideAspect(bigMomma);
    }
}
