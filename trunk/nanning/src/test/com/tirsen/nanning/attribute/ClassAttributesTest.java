package com.tirsen.nanning.attribute;

import junit.framework.TestCase;

public class ClassAttributesTest extends TestCase {
    private ClassAttributes classAttributes;

    protected void setUp() throws Exception {
        super.setUp();

        classAttributes = new ClassAttributes();
    }

    public void testLoadFieldAttribute() {
        classAttributes.loadFieldAttribute("field", "attribute", "value");
        assertEquals("value", classAttributes.properties.getProperty("field.field.attribute"));
    }

    public void testLoadMethodAttribute() {
        classAttributes.loadMethodAttribute("method()", "attribute", "value");
        assertEquals("value", classAttributes.properties.getProperty("method.method().attribute"));
    }

    public void testLoadClassAttribute() {
        classAttributes.loadClassAttribute("attribute", "value");
        assertEquals("value", classAttributes.properties.getProperty("class.attribute"));
    }

    public void testMethodSignature() {
        assertEquals("method()", ClassAttributes.methodSignature(AttributesTest.method));
        assertEquals("method(String,String)", ClassAttributes.methodSignature(AttributesTest.argMethod));
        assertEquals("method(String)", ClassAttributes.methodSignature(AttributesTest.arrayArgMethod));
    }

    public void testGetName() {
        assertEquals(null, classAttributes.getName());
        classAttributes.setName("Class");
        assertEquals("Class", classAttributes.getName());

        classAttributes = new ClassAttributes(AttributesTestClass.class);
        assertEquals("AttributesTestClass", classAttributes.getName());
        classAttributes = new ClassAttributes(AttributesTestClass.InnerClass.class);
        assertEquals("AttributesTestClass$InnerClass", classAttributes.getName());
    }

}
