package com.tirsen.nanning.config;

import com.tirsen.nanning.MixinInstance;
import com.tirsen.nanning.attribute.AbstractAttributesTest;

import java.lang.reflect.Method;

public class PointcutTest extends AbstractAttributesTest {

    public static interface Interface {
        void method();
    }

    public void testFalseAbstractPointcut() {
        Pointcut falsePointcut = new AbstractPointcut() {
            public boolean adviseMethod(Method method) {
                return false;
            }
        };
        assertEquals(0, falsePointcut.methodsToAdvise(null, new MixinInstance(Interface.class, null)).length);
    }

    public void testTrueAbstractPointcut() {
        Pointcut falsePointcut = new AbstractPointcut() {
            public boolean adviseMethod(Method method) {
                return true;
            }
        };
        assertEquals(1, falsePointcut.methodsToAdvise(null, new MixinInstance(Interface.class, null)).length);
    }

    public void testAttributePointcut() throws NoSuchMethodException {
        AttributePointcut attributePointcut = new AttributePointcut("attribute");
        Method methodWithAttribute = AttributesTestClass.class.getMethod("methodWithAttribute", null);
        Method methodWithoutAttribute = AttributesTestClass.class.getMethod("methodWithoutAttribute", null);
        assertTrue(attributePointcut.adviseMethod(methodWithAttribute));
        assertFalse(attributePointcut.adviseMethod(methodWithoutAttribute));
    }


}
