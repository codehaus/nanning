package org.codehaus.nanning.xml;

import org.codehaus.nanning.config.AspectSystem;
import org.codehaus.nanning.AspectInstance;
import org.codehaus.nanning.Aspects;
import org.codehaus.nanning.Mixin;
import org.codehaus.nanning.NullInterceptor;
import org.codehaus.nanning.attribute.AbstractAttributesTest;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Acceptance test for xml-package.
 */
public class XMLTest extends AbstractAttributesTest {
    private Method method;
    private AspectSystemParser aspectSystemParser;

    protected void setUp() throws Exception {
        super.setUp();

        method = Interface.class.getMethod("method", null);
        aspectSystemParser = new AspectSystemParser();
    }

    public void testFunctional() throws IOException {
        AspectSystem aspectSystem = aspectSystemParser.parse(getClass().getResource("aspect-system-test.xml"));

        Interface o = (Interface) aspectSystem.newInstance(Interface.class);

        AspectInstance instance = Aspects.getAspectInstance(o);
        assertEquals(1, instance.getMixins().size());
        Mixin mixinInstance = (Mixin) instance.getMixins().get(0);
        assertEquals(2, mixinInstance.getInterceptorsForMethod(method).size());
        assertTrue(mixinInstance.getInterceptorsForMethod(method).get(0) instanceof NullInterceptor);
        assertTrue(mixinInstance.getInterceptorsForMethod(method).get(1) instanceof NullInterceptor);
    }
}
