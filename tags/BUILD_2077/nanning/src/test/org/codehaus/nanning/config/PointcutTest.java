package org.codehaus.nanning.config;

import org.codehaus.nanning.Mixin;
import org.codehaus.nanning.AspectInstance;
import org.codehaus.nanning.MethodInterceptor;
import org.codehaus.nanning.Invocation;
import org.codehaus.nanning.attribute.AbstractAttributesTest;

import java.lang.reflect.Method;

public class PointcutTest extends AbstractAttributesTest {
    private Method methodWithAttribute;
    private Method methodWithoutAttribute;
    private Method method;

    public static interface Interface {
        void method();
    }

    public interface InterfaceWithAttributes {
        /**
         * @attribute
         */
        void methodWithAttribute();

        void methodWithoutAttribute();
    }

    public void testAttribute() throws NoSuchMethodException {
        Pointcut attributePointcut = P.methodAttribute("attribute");

        assertTrue(attributePointcut.adviseMethod(null, null, methodWithAttribute));
        assertFalse(attributePointcut.adviseMethod(null, null, methodWithoutAttribute));
    }

    public void testAll() {
        assertTrue(P.all().adviseMethod(null, null, null));
    }

    public void testEmpty() {
        assertFalse(P.empty().adviseMethod(null, null, null));
    }

    public void testNot() {
        assertFalse(P.not(P.all()).adviseMethod(null, null, null));
        assertTrue(P.not(P.not(P.all())).adviseMethod(null, null, null));
    }

    public void testAnd() {
        assertTrue(P.and(P.all(), P.all()).adviseMethod(null, null, null));
        assertFalse(P.and(P.empty(), P.empty()).adviseMethod(null, null, null));
        assertFalse(P.and(P.all(), P.empty()).adviseMethod(null, null, null));
        assertFalse(P.and(P.empty(), P.all()).adviseMethod(null, null, null));
    }

    public void testOr() {
        assertTrue(P.or(P.all(), P.all()).adviseMethod(null, null, null));
        assertFalse(P.or(P.empty(), P.empty()).adviseMethod(null, null, null));
        assertTrue(P.or(P.all(), P.empty()).adviseMethod(null, null, null));
        assertTrue(P.or(P.empty(), P.all()).adviseMethod(null, null, null));
    }

    public void testMethodName() {
        assertTrue(P.methodName("method.*").adviseMethod(null, null, methodWithAttribute));
        assertFalse(P.methodName(".*Without.*").adviseMethod(null, null, methodWithAttribute));
        assertTrue(P.methodName(".*With.*").adviseMethod(null, null, methodWithoutAttribute));
    }

    public void testIsMixinInterfaceSelectsMethods() {
        assertTrue(P.isMixinInterface(Interface.class).adviseMethod(null, new Mixin(Interface.class, null), null));
        assertFalse(P.isMixinInterface(InterfaceWithAttributes.class).adviseMethod(null, new Mixin(Interface.class, null), null));
    }

    public void testIsMixinInterfaceSelectsInstance() {
        AspectInstance instance = new AspectInstance();
        instance.addMixin(new Mixin(Interface.class, null));
        assertTrue(P.isMixinInterface(Interface.class).introduceOn(instance));

        instance = new AspectInstance();
        instance.addMixin(new Mixin(InterfaceWithAttributes.class, null));
        assertFalse(P.isMixinInterface(Interface.class).introduceOn(instance));
    }

    public void testComplicatedPointcut() {
        Pointcut pointcut = P.and(P.methodName("method.*"), P.not(P.isMixinInterface(Interface.class)));

        AspectInstance instance = new AspectInstance();
        Mixin mixin = new Mixin(Interface.class, null);
        instance.addMixin(mixin);
        assertFalse(pointcut.adviseMethod(instance, mixin, method));

        instance = new AspectInstance();
        mixin = new Mixin(InterfaceWithAttributes.class, null);
        instance.addMixin(mixin);
        assertTrue(pointcut.adviseMethod(instance, mixin, methodWithAttribute));
        assertTrue(pointcut.adviseMethod(instance, mixin, methodWithoutAttribute));
    }


    protected void setUp() throws Exception {
        super.setUp();

        methodWithAttribute = InterfaceWithAttributes.class.getMethod("methodWithAttribute", null);
        methodWithoutAttribute = InterfaceWithAttributes.class.getMethod("methodWithoutAttribute", null);
        method = Interface.class.getMethod("method", null);
    }

    public void testIntroduceTruePointcut() {
        Pointcut allPointcut = new Pointcut() {
            public boolean adviseMethod(AspectInstance instance, Mixin mixin, Method method) {
                return true;
            }

            public boolean introduceOn(AspectInstance instance) {
                return true;
            }
        };
        AspectInstance instance = new AspectInstance();
        assertTrue(instance.getMixins().isEmpty());
        Mixin mixin = new Mixin(Interface.class, null);
        allPointcut.introduce(instance, mixin);
        assertFalse(instance.getMixins().isEmpty());
        assertSame(mixin, instance.getMixins().get(0));
    }

    public void testAdviseFalsePointcut() {
        Pointcut falsePointcut = new Pointcut() {
            public boolean adviseMethod(AspectInstance instance, Mixin mixin, Method method) {
                return false;
            }
        };

        AspectInstance aspectInstance = new AspectInstance();
        aspectInstance.addMixin(new Mixin(Interface.class, null));

        assertEquals(0, aspectInstance.getAllInterceptors().size());
        falsePointcut.advise(aspectInstance, new MethodInterceptor() {
            public Object invoke(Invocation invocation) throws Throwable {
                return null;
            }
        });
        assertEquals(0, aspectInstance.getAllInterceptors().size());
    }

    public void testAdviseTruePointcut() {
        Pointcut truePointcut = new Pointcut() {
            public boolean adviseMethod(AspectInstance instance, Mixin mixin, Method method) {
                return true;
            }
        };

        AspectInstance aspectInstance = new AspectInstance();
        aspectInstance.addMixin(new Mixin(Interface.class, null));

        assertEquals(0, aspectInstance.getAllInterceptors().size());
        truePointcut.advise(aspectInstance, new MethodInterceptor() {
            public Object invoke(Invocation invocation) throws Throwable {
                return null;
            }
        });
        assertEquals(1, aspectInstance.getAllInterceptors().size());
    }
}
