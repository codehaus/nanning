package org.codehaus.nanning.xml;

import org.codehaus.nanning.*;
import org.codehaus.nanning.config.*;
import junit.framework.TestCase;

import java.io.IOException;

public class AspectSystemParserTest extends TestCase {
    private AspectSystemParser aspectSystemParser;

    protected void setUp() throws Exception {
        super.setUp();

        aspectSystemParser = new AspectSystemParser();
    }

    public static class TestAspect implements Aspect {
        public void introduce(AspectInstance aspectInstance) {
        }

        public void advise(AspectInstance aspectInstance) {
        }
    }

    public static class TestInterceptor implements MethodInterceptor {
        public Object invoke(Invocation invocation) throws Throwable {
            return null;
        }
    }

    public void testParseEmptyAspectSystem() throws IOException {
        String xml = "<aspect-system />";

        aspectSystemParser = new AspectSystemParser();
        assertTrue(aspectSystemParser.parse(xml) instanceof AspectSystem);
    }

    public void testParseAspect() throws IOException {
        String xml = "<aspect-system><aspect class='" + TestAspect.class.getName() + "' /></aspect-system>";
        AspectSystem aspectSystem = aspectSystemParser.parse(xml);

        assertEquals(1, aspectSystem.getAspects().size());
        assertTrue(aspectSystem.getAspects().get(0) instanceof TestAspect);
    }

    public void testParseInterceptor() throws IOException {
        String xml = "<aspect-system><interceptor class=\"" + TestInterceptor.class.getName() + "\" /></aspect-system>";
        AspectSystem aspectSystem = aspectSystemParser.parse(xml);

        assertEquals(1, aspectSystem.getAspects().size());
        assertTrue(aspectSystem.getAspects().get(0) instanceof InterceptorAspect);

        InterceptorAspect interceptorAspect = (InterceptorAspect) aspectSystem.getAspects().get(0);
        assertEquals(TestInterceptor.class, interceptorAspect.getInterceptorClass());
        assertEquals(InterceptorAspect.PER_METHOD, interceptorAspect.getStateManagement());
    }

    public void testParseInterceptorWithPointcut() throws IOException {
        String xml = "<aspect-system><interceptor class='" + TestInterceptor.class.getName() + "'>" +
                "<pointcut attribute='attribute'></pointcut>" +
                "</interceptor></aspect-system>";
        AspectSystem aspectSystem = aspectSystemParser.parse(xml);
        InterceptorAspect interceptorAspect = (InterceptorAspect) aspectSystem.getAspects().get(0);
        assertTrue(interceptorAspect.getPointcut() instanceof P.MethodAttribute);
        P.MethodAttribute attributePointcut = (P.MethodAttribute) interceptorAspect.getPointcut();
        assertEquals("attribute", attributePointcut.getAttribute());
    }

    public void testParseClass() throws IOException {
        String xml = "<aspect-system><class name='" + Interface.class.getName() + "' /></aspect-system>";
        AspectSystem aspectSystem = aspectSystemParser.parse(xml);
        ClassAspect classAspect = (ClassAspect) aspectSystem.getAspects().get(0);
        assertEquals(Interface.class, classAspect.getClassIdentifier());
    }

    public void testParseClassWithLocalAspect() throws IOException {
        String xml = "<aspect-system><class name='" + Interface.class.getName() + "'>" +
                "<aspect class='" + TestAspect.class.getName() + "' /></class></aspect-system>";
        AspectSystem aspectSystem = aspectSystemParser.parse(xml);
        ClassAspect classAspect = (ClassAspect) aspectSystem.getAspects().get(0);
        assertEquals(Interface.class, classAspect.getClassIdentifier());
        assertEquals(1, classAspect.getAspects().size());
        assertTrue(classAspect.getAspects().get(0) instanceof TestAspect);
    }

    public void testParseMixin() throws IOException {
        String xml = "<aspect-system><mixin interface='" + Interface.class.getName() + "' " +
                "target='" + Target.class.getName() + "' /></aspect-system>";
        AspectSystem aspectSystem = aspectSystemParser.parse(xml);
        assertEquals(1, aspectSystem.getAspects().size());
        Introductor introductor = (Introductor) aspectSystem.getAspects().get(0);
        assertEquals(Interface.class, introductor.getInterfaceClass());
        assertEquals(Target.class, introductor.getTargetClass());
    }
}
