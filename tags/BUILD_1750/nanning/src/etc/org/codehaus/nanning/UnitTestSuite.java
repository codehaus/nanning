/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.codehaus.nanning;

import org.codehaus.nanning.attribute.AttributesBuilderTest;
import org.codehaus.nanning.attribute.AttributesXMLParserTest;
import org.codehaus.nanning.attribute.ClassPropertiesHelperTest;
import org.codehaus.nanning.config.AspectSystemTest;
import org.codehaus.nanning.config.InterceptorAspectTest;
import org.codehaus.nanning.config.PointcutTest;
import org.codehaus.nanning.cache.CacheInterceptorTest;
import org.codehaus.nanning.cache.CacheTest;
import org.codehaus.nanning.prevayler.ObjectGraphVisitorTest;
import org.codehaus.nanning.xml.AspectSystemParserTest;
import org.codehaus.nanning.locking.PessimisticLockingAspectTest;
import org.codehaus.nanning.definition.*;
import org.codehaus.nanning.profiler.ProfilerTest;
import org.codehaus.nanning.remote.RemoteTest;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * TODO document UnitTestSuite
 *
 * <!-- $Id: UnitTestSuite.java,v 1.1 2003-07-04 10:53:56 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.1 $
 */
public class UnitTestSuite {
    ///CLOVER:OFF
    public static Test suite() {
        TestSuite suite = new TestSuite();

        suite.addTestSuite(ClassPropertiesHelperTest.class);
        suite.addTestSuite(AttributesBuilderTest.class);
        suite.addTestSuite(AttributesXMLParserTest.class);

        suite.addTestSuite(AspectInstanceTest.class);
        suite.addTestSuite(ObjectGraphVisitorTest.class);
        suite.addTestSuite(SerializationTest.class);
        suite.addTestSuite(org.codehaus.nanning.remote.RemoteTest.class);
        suite.addTestSuite(AspectClassTest.class);
        suite.addTestSuite(ConstructionInterceptorTest.class);
        suite.addTestSuite(InheritanceTest.class);
        suite.addTestSuite(InterceptorTest.class);
        suite.addTestSuite(SerializationTest.class);
        suite.addTestSuite(AttributeFilterTest.class);
        suite.addTestSuite(AspectRepositoryTest.class);
        suite.addTestSuite(AspectsTest.class);
        suite.addTestSuite(AttributeFilterTest.class);
        suite.addTestSuite(MethodFilterTest.class);

        suite.addTestSuite(ProfilerTest.class);

        suite.addTestSuite(CacheTest.class);
        suite.addTestSuite(CacheInterceptorTest.class);

        suite.addTestSuite(RemoteTest.class);

        suite.addTestSuite(AspectSystemTest.class);
        suite.addTestSuite(PointcutTest.class);
        suite.addTestSuite(InterceptorAspectTest.class);

        suite.addTestSuite(AspectSystemParserTest.class);

        suite.addTestSuite(PessimisticLockingAspectTest.class);
        
        return suite;
    }
    ///CLOVER:ON
}
