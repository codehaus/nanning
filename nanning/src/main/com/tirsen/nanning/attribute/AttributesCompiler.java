/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.attribute;

import com.thoughtworks.qdox.parser.impl.JFlexLexer;
import com.thoughtworks.qdox.parser.impl.Parser;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;

import java.io.*;
import java.util.List;
import java.util.Iterator;

/**
 * TODO document AttributesCompiler
 *
 * <!-- $Id: AttributesCompiler.java,v 1.9 2003-06-12 14:18:17 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.9 $
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

}