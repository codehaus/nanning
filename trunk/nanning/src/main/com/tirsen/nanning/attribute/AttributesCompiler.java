/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.attribute;

import java.io.*;
import java.util.Properties;

import com.thoughtworks.qdox.parser.impl.JFlexLexer;
import com.thoughtworks.qdox.parser.impl.Parser;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;

/**
 * TODO document AttributesCompiler
 *
 * <!-- $Id: AttributesCompiler.java,v 1.6 2003-06-09 17:40:41 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.6 $
 */
public class AttributesCompiler extends Task {
    private File src;
    private File dest;
    private AttributesBuilder builder = new AttributesBuilder();

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
                    createAttributesFile(javaFile, attributeFile);
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
        File result = new File(dest, javaFileName.substring(0, javaFileName.length() - 5) + ".attributes");
        result.getParentFile().mkdirs();
        return result;
    }

    private void createAttributesFile(File javaFile, File attributeFile) throws IOException {
        ClassAttributes attributes = parseClassAttribute(javaFile);
        OutputStream output = new FileOutputStream(attributeFile);
        try {
            attributes.store(output, javaFile.getName());
        } finally {
            output.close();
        }
    }

    ClassAttributes parseClassAttribute(File javaFile) throws IOException {
        InputStream input = new FileInputStream(javaFile);
        try {
            builder.reset();
            new Parser(new JFlexLexer(input), builder).parse();
            return builder.getClassAttributes();
        } finally {
            input.close();
        }
    }

}