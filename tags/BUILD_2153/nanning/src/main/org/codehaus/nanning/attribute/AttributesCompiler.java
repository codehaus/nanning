/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.codehaus.nanning.attribute;

import com.thoughtworks.qdox.parser.impl.JFlexLexer;
import com.thoughtworks.qdox.parser.impl.Parser;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;

import java.io.*;
import java.util.List;
import java.util.Iterator;

/**
 * Compiles attributes from java sources, use as an ant task or directly from java.
 * <p>
 * Example usage as Ant task: <pre>
        <taskdef name="attributes-compiler" classname="org.codehaus.nanning.attributes.AttributesCompiler"
            classpath="path/to/lib/nanning-version.jar:path/to/lib/qdox-1.2.jar" />
        <attributes-compiler src="path/to/java/files" dest="path/to/compile/target" />
</pre>
 * <p>
 * Example usage directly from Java: <pre>
        AttributesCompiler attributesCompiler = new AttributesCompiler();
        attributesCompiler.setSrc(source);
        attributesCompiler.setDest(attributesDir);
        attributesCompiler.execute();
</pre>
 *
 * After compilation, the destination-directory needs to be included in the classpath.
 * <p>
 * To use under Maven, add a preGoal to java:compile and test:compile that executes
 * the above ant-code.
 * <p> 
 * To use directly in JUnit (without the need for external recompilation
 * good for execution within an IDE), write an abstract base-class for your 
 * tests that executes the above Java-code to compile your attributes before 
 * the actual tests are executed.
 *
 * <!-- $Id: AttributesCompiler.java,v 1.3 2003-09-22 14:36:39 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.3 $
 */
public class AttributesCompiler extends Task {
    private File src;
    private File dest;

    public void setSrc(File src) {
        this.src = src;
    }

    public void setDest(File dest) {
        this.dest = dest;
    }

    public void execute() {
        try {
            boolean hasCompiled = false;

            String[] files = getJavaFiles();
            for (int i = 0; i < files.length; i++) {
                final File javaFile = new File(src, files[i]);
                final File attributeFile = getAttributeFile(files[i]);
                if (!attributeFile.exists() || attributeFile.lastModified() < javaFile.lastModified()) {
                    createAttributeFiles(javaFile);
                    if (!hasCompiled) {
                        System.out.println("Compiling attributes for " + src + " into " + dest);
                        hasCompiled = true;
                    }
                }
            }
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }

    private String[] getJavaFiles() {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(src);
        scanner.setIncludes(new String[]{"**/*.java"});
        scanner.scan();
        return scanner.getIncludedFiles();
    }

    private File getAttributeFile(String javaFileName) {
        File result = new File(dest, javaFileName.substring(0, javaFileName.length() - 5) +
                                     PropertyFileAttributeLoader.ATTRIBUTE_FILE_SUFFIX);
        result.getParentFile().mkdirs();
        return result;
    }

    private void createAttributeFiles(File javaFile) throws IOException {
        List result = parseClassAttribute(javaFile);
        for (Iterator iterator = result.iterator(); iterator.hasNext();) {
            ClassPropertiesHelper properties = (ClassPropertiesHelper) iterator.next();
            properties.store(dest);
        }
    }

    List parseClassAttribute(File javaFile) throws IOException {
        InputStream input = new FileInputStream(javaFile);
        try {
            AttributesBuilder builder = new AttributesBuilder();
            new Parser(new JFlexLexer(input), builder).parse();
            return builder.getClassPropertiesHelpers();
        } finally {
            input.close();
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: AttributesCompiler <source-directory> <destination-directory>");
            System.exit(0);
        }
        AttributesCompiler compiler = new AttributesCompiler();
        compiler.setSrc(new File(args[0]));
        compiler.setDest(new File(args[1]));
        compiler.execute();
    }
}