/*
 * $Header: /home/projects/nanning/scm-cvs/nanning/src/main/com/tirsen/nanning/AttributesCompiler.java,v 1.4 2002-12-03 07:50:16 tirsen Exp $
 * $Revision: 1.4 $
 * $Date: 2002-12-03 07:50:16 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * $Id: AttributesCompiler.java,v 1.4 2002-12-03 07:50:16 tirsen Exp $
 */
package com.tirsen.nanning;

import com.thoughtworks.qdox.parser.impl.Parser;
import com.thoughtworks.qdox.parser.impl.JFlexLexer;

import java.io.*;
import java.util.Properties;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;

/**
 * <p><code>AttributesCompilerOld</code> is an Ant Task which
 * uses QDox to generate the attributes files used by the default
 * DefaultAttributeFinder implementation
 *
 * @author <a href="mailto:jon_tirsen@yahoo.com">Jon Tirs�n</a>
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.4 $
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
			System.out.println("Compiling attributes for " + src + " into " + dest);
			String[] files = getJavaFiles();
			for (int i = 0; i < files.length; i++) {
				final File javaFile = new File(src, files[i]);
				final File attributeFile = getAttributeFile(files[i]);
				if (!attributeFile.exists() || attributeFile.lastModified() < javaFile.lastModified()) {
					createAttributesFile(javaFile, attributeFile);
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
		Properties attributes = parseAttributeProperties(javaFile);
		OutputStream output = new FileOutputStream(attributeFile);
		try {
			attributes.store(output, javaFile.getName());
		} finally {
			output.close();
		}
	}

	private Properties parseAttributeProperties(File javaFile) throws IOException {
		InputStream input = new FileInputStream(javaFile);
		try {
			builder.reset();
			new Parser(new JFlexLexer(input), builder).parse();
			return builder.getProperties();
		} finally {
			input.close();
		}
	}

}
