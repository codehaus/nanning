package org.codehaus.nanning.attribute;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import com.thoughtworks.qdox.parser.impl.JFlexLexer;
import com.thoughtworks.qdox.parser.impl.Parser;
import junit.framework.TestCase;

public class AttributesBuilderTest extends TestCase {
    private List classPropertiesHelpers;
    private AttributesBuilder builder;
    private FileInputStream input;

    protected void setUp() throws Exception {
        super.setUp();

        builder = new AttributesBuilder();
        File javaFile = AbstractAttributesTest.findNanningFile("src" + File.separator +
                                                               "test" + File.separator +
                                                               "org" + File.separator +
                                                               "codehaus" + File.separator +
                                                               "nanning" + File.separator +
                                                               "attribute" + File.separator +
                                                               "AttributesTestClass.java");

        input = new FileInputStream(javaFile);
        new Parser(new JFlexLexer(input), builder).parse();

        classPropertiesHelpers = builder.getClassPropertiesHelpers();
        assertEquals(2, classPropertiesHelpers.size());
    }

    protected void tearDown() throws Exception {
        input.close();

        super.tearDown();
    }

    public void testOuterClass() throws IOException {
        ClassPropertiesHelper classPropertiesHelper = (ClassPropertiesHelper) classPropertiesHelpers.get(0);
        assertEquals("AttributesTestClass", classPropertiesHelper.getClassName());
        assertEquals("org.codehaus.nanning.attribute", classPropertiesHelper.getPackageName());

        ClassAttributes attributes = new ClassAttributes(AttributesTestClass.class);
        classPropertiesHelper.setTargetClassAttributes(attributes);
        classPropertiesHelper.transferAttributesToTarget();

        assertEquals(AttributesTest.FIELD_VALUE,
                     attributes.getAttribute(AttributesTest.field, AttributesTest.FIELD_ATTRIBUTE));
        assertEquals(AttributesTest.METHOD_VALUE,
                     attributes.getAttribute(AttributesTest.method, AttributesTest.METHOD_ATTRIBUTE));
    }

    public void testInnerClass() throws IOException {
        ClassPropertiesHelper classPropertiesHelper = (ClassPropertiesHelper) classPropertiesHelpers.get(1);
        assertEquals("AttributesTestClass$InnerClass", classPropertiesHelper.getClassName());
        assertEquals("org.codehaus.nanning.attribute", classPropertiesHelper.getPackageName());

        ClassAttributes attributes = new ClassAttributes(AttributesTestClass.InnerClass.class);
        classPropertiesHelper.setTargetClassAttributes(attributes);
        classPropertiesHelper.transferAttributesToTarget();

        assertEquals(AttributesTest.INNER_VALUE, attributes.getAttribute(AttributesTest.INNER_ATTRIBUTE));
        assertEquals(AttributesTest.INNER_FIELD_VALUE, attributes.getAttribute(AttributesTest.innerField, AttributesTest.INNER_FIELD_ATTRIBUTE));
    }

}
