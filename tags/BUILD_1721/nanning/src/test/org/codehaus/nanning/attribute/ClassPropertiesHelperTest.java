package org.codehaus.nanning.attribute;

import junit.framework.TestCase;

import java.lang.reflect.Proxy;
import java.io.File;

public class ClassPropertiesHelperTest extends TestCase {
    private ClassPropertiesHelper classPropertiesHelper;

    protected void setUp() throws Exception {
        super.setUp();

        classPropertiesHelper = new ClassPropertiesHelper();
    }

    public void testLoadFieldAttribute() {
        classPropertiesHelper.loadFieldAttribute("field", "attribute", "value");
        assertEquals("value", classPropertiesHelper.properties.getProperty("field.field.attribute"));
    }

    public void testLoadMethodAttribute() {
        classPropertiesHelper.loadMethodAttribute("method()", "attribute", "value");
        assertEquals("value", classPropertiesHelper.properties.getProperty("method.method().attribute"));
    }

    public void testLoadClassAttribute() {
        classPropertiesHelper.loadClassAttribute("attribute", "value");
        assertEquals("value", classPropertiesHelper.properties.getProperty("class.attribute"));
    }

    void method(double[] doubles) {}
    void method(Double[] doubles) {}
    void method(int[] ints) {}

    public void testMethodSignature() throws NoSuchMethodException {
        assertEquals("method()", ClassPropertiesHelper.methodSignature(AttributesTest.method));
        assertEquals("method()", ClassPropertiesHelper.methodSignature(AttributesTest.method));
        assertEquals("method(String,String)", ClassPropertiesHelper.methodSignature(AttributesTest.argMethod));
        assertEquals("method(String,String)", ClassPropertiesHelper.methodSignature(AttributesTest.argMethod));
        assertEquals("method(String)", ClassPropertiesHelper.methodSignature(AttributesTest.arrayArgMethod));
        assertEquals("method(String)", ClassPropertiesHelper.methodSignature(AttributesTest.arrayArgMethod));
        assertEquals("method(double)", ClassPropertiesHelper.methodSignature(
                ClassPropertiesHelperTest.class.getDeclaredMethod("method", new Class[] { double[].class })));
        assertEquals("method(Double)", ClassPropertiesHelper.methodSignature(
                ClassPropertiesHelperTest.class.getDeclaredMethod("method", new Class[] { Double[].class })));
        assertEquals("method(int)", ClassPropertiesHelper.methodSignature(
                ClassPropertiesHelperTest.class.getDeclaredMethod("method", new Class[] { int[].class })));
    }

    public void testGetClassName() {
        assertEquals(null, classPropertiesHelper.getClassName());
        classPropertiesHelper.setClassName("Class");
        assertEquals("Class", classPropertiesHelper.getClassName());
    }

    public void testJoinTail() {
        String[] parts = "field.field.field.attribute".split("\\.");
        assertEquals("field.attribute", ClassPropertiesHelper.joinTail(parts, 2));
        parts = "field.field.attribute".split("\\.");
        assertEquals("attribute", ClassPropertiesHelper.joinTail(parts, 2));
    }

    public void testClassName() {
        assertEquals("ClassPropertiesHelperTest", ClassPropertiesHelper.className(ClassPropertiesHelperTest.class));
    }

    public void testPackageName() {
        assertEquals("org.codehaus.nanning.attribute", ClassPropertiesHelper.packageName(ClassPropertiesHelperTest.class));
        assertEquals("", ClassPropertiesHelper.packageName(
                Proxy.getProxyClass(getClass().getClassLoader(), new Class[0])));
    }


    public void testGetFileName() {
        File baseDir = new File(".");
        classPropertiesHelper.setPackageName("package.name");
        classPropertiesHelper.setClassName("ClassName");
        assertEquals(new File(baseDir, "package" + File.separator + "name" + File.separator + "ClassName.attributes"),
                     classPropertiesHelper.getAttributeFile(baseDir));
    }
}