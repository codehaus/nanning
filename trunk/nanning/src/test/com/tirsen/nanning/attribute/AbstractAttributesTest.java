package com.tirsen.nanning.attribute;

import java.io.File;
import java.net.MalformedURLException;

import junit.framework.TestCase;

public abstract class AbstractAttributesTest extends TestCase {
    private static boolean attributesCompiled = false;
    private static File attributesDir;

    protected void setUp() throws Exception {
        super.setUp();
        compileAttributes();
    }

    private static void compileAttributes() {
        if (!attributesCompiled) {
            attributesCompiled = true;
            attributesDir = new File("target" + File.separator + "attributes");
            try {
                Attributes.addSearchPath(attributesDir.toURL());
            } catch (MalformedURLException e) {
                fail(e.getMessage());
            }
            compileAttributes(new File("src" + File.separator + "test"));
            compileAttributes(new File("src" + File.separator + "main"));
            compileAttributes(new File(".." + File.separator + "nanning" + File.separator + "src" + File.separator + "main"));
            compileAttributes(new File(".." + File.separator + "nanning" + File.separator + "src" + File.separator + "test"));
        }
    }

    private static void compileAttributes(File source) {
        if (source.isDirectory()) {
            AttributesCompiler attributesCompiler = new AttributesCompiler();
            attributesCompiler.setSrc(source);
            attributesCompiler.setDest(attributesDir);
            attributesCompiler.execute();
        }
    }
}
