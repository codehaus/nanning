package com.tirsen.nanning.attribute;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;

public class AttributesCompilerTest extends TestCase {

    public void testParseClassAttribute() throws IOException {

        AttributesCompiler attributesCompiler = new AttributesCompiler();
        File javaFile = new File("src" + File.separator +
                                 "test" + File.separator +
                                 "com" + File.separator +
                                 "tirsen" + File.separator +
                                 "nanning" + File.separator +
                                 "attribute" + File.separator +
                                 "AttributesTestClass.java");
        ClassPropertiesHelper classPropertiesHelper = attributesCompiler.parseClassAttribute(javaFile);

//        assertEquals("AttributesTestClass", attributes.getClassName());
//        assertEquals("com.tirsen.nanning.attribute", attributes.getPackageName());

        ClassAttributes attributes = new ClassAttributes(AttributesTestClass.class);
        classPropertiesHelper.setTargetClassAttributes(attributes);
        classPropertiesHelper.transferAttributesToTarget();

        assertEquals(AttributesTest.FIELD_VALUE,
                attributes.getAttribute(AttributesTest.field, AttributesTest.FIELD_ATTRIBUTE));
        assertEquals(AttributesTest.METHOD_VALUE,
                attributes.getAttribute(AttributesTest.method, AttributesTest.METHOD_ATTRIBUTE));

    }

}
