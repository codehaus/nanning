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

            compileFromBaseDir(new File("."));
            compileFromBaseDir(new File(".." + File.separator + "nanning"));

            try {
                Attributes.addSearchPath(attributesDir.toURL());
            } catch (MalformedURLException e) {
                fail(e.getMessage());
            }
        }
    }

    private static void compileFromBaseDir(File baseDir) {
        compileAttributes(new File(baseDir, "src" + File.separator + "test"));
        compileAttributes(new File(baseDir, "src" + File.separator + "main"));
        File[] frameworks = new File(baseDir, "src" + File.separator + "frameworks").listFiles();
        if (frameworks != null) {
            for (int i = 0; i < frameworks.length; i++) {
                File framework = frameworks[i];
                compileAttributes(new File(framework, "src" + File.separator + "test"));
                compileAttributes(new File(framework, "src" + File.separator + "main"));
            }
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

    public static File findNanningFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            return file;
        }
        file = new File(".." + File.separator + "nanning", path);
        return file;
    }
}
