package com.tirsen.nanning.attribute;

import junit.framework.TestCase;

import java.io.File;
import java.net.MalformedURLException;

public abstract class AbstractAttributesTest extends TestCase {
    private static boolean attributesCompiled = false;

    protected void setUp() throws Exception {
        super.setUp();
        compileAttributes();
    }

    private static void compileAttributes() {
        if (!attributesCompiled) {
            attributesCompiled = true;
            File targetDir = new File("target" + File.separator + "attributes");
            try {
                Attributes.addSearchPath(targetDir.toURL());
            } catch (MalformedURLException e) {
                fail(e.getMessage());
            }
            AttributesCompiler attributesCompiler = new AttributesCompiler();
            attributesCompiler.setSrc(new File("src" + File.separator + "test"));
            attributesCompiler.setDest(targetDir);
            attributesCompiler.execute();
            attributesCompiler.setSrc(new File("src" + File.separator + "main"));
            attributesCompiler.execute();
            attributesCompiler.setSrc(new File(".." + File.separator + "nanning" + File.separator + "src" + File.separator + "main"));
            attributesCompiler.execute();
            attributesCompiler.setSrc(new File(".." + File.separator + "nanning" + File.separator + "src" + File.separator + "test"));
            attributesCompiler.execute();
        }
    }
}
