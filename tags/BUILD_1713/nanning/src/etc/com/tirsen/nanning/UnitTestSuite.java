/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import com.tirsen.nanning.attribute.AttributesBuilderTest;
import com.tirsen.nanning.attribute.AttributesXMLParserTest;
import com.tirsen.nanning.attribute.ClassPropertiesHelperTest;
import com.tirsen.nanning.config.AspectSystemTest;
import com.tirsen.nanning.config.InterceptorAspectTest;
import com.tirsen.nanning.config.PointcutTest;
import com.tirsen.nanning.samples.CacheInterceptorTest;
import com.tirsen.nanning.samples.CacheTest;
import com.tirsen.nanning.prevayler.ObjectGraphVisitorTest;
import com.tirsen.nanning.xml.AspectSystemParserTest;
import com.tirsen.nanning.locking.PessimisticLockingAspectTest;
import com.tirsen.nanning.definition.*;
import com.tirsen.nanning.profiler.ProfilerTest;
import com.tirsen.nanning.remote.RemoteTest;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * TODO document UnitTestSuite
 *
 * <!-- $Id: UnitTestSuite.java,v 1.19 2003/05/22 20:18:35 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.19 $
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
        suite.addTestSuite(com.tirsen.nanning.remote.RemoteTest.class);
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
