/*
 * $Header: /home/projects/nanning/scm-cvs/nanning/src/main/com/tirsen/nanning/AttributesBuilder.java,v 1.1 2002-12-03 07:50:16 tirsen Exp $
 * $Revision: 1.1 $
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
 * $Id: AttributesBuilder.java,v 1.1 2002-12-03 07:50:16 tirsen Exp $
 */
package com.tirsen.nanning;

import java.util.Properties;
import java.util.Iterator;

import com.thoughtworks.qdox.parser.Builder;
import com.thoughtworks.qdox.parser.structs.ClassDef;
import com.thoughtworks.qdox.parser.structs.FieldDef;
import com.thoughtworks.qdox.parser.structs.MethodDef;

/**
 * QDox Builder implementation for creating Properties containing attributes.
 *
 * <p>This Builder should be fed to the QDox Parser where it shall receive callbacks as a source file is parsed.
 * After the file has been parsed, getProperties() can be called to retrieved the compiled properties of the class.</p>
 *
 * <p>An AttributesBuilder can only be used to parse <b>one</b> file at a time. If the AttributesBuilder is to be reused
 * to parse another file, the reset() method must be called.</p>
 *
 * @author <a href="joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.1 $
 */
public class AttributesBuilder implements Builder {

	private final Properties properties = new Properties();
	private final Properties currentAttributes = new Properties();
    private static final String SEPARATOR = ".";

    // Methods needed to implement Builder that we don't care about.
	public void addPackage(String packageName) {
	}

	public void addImport(String importName) {
	}

	public void addJavaDoc(String text) {
	}

	public void endClass() {
	}

	public void addJavaDocTag(String tag, String text) {
		currentAttributes.setProperty(tag, text);
	}

	public void beginClass(ClassDef def) {
		addCurrentAttributes("class");
	}

	public void addMethod(MethodDef def) {
		final StringBuffer method = new StringBuffer(def.name);
		method.append('(');
		for (Iterator params = def.params.iterator(); params.hasNext();) {
            FieldDef param = (FieldDef) params.next();
			method.append(param.type);
			method.append(',');
		}
		if (def.params.size() > 0) {
			// trim last comma
			method.setLength(method.length() - 1);
		}
		method.append(')');
		addCurrentAttributes(method.toString());
	}

	public void addField(FieldDef def) {
		addCurrentAttributes(def.name);
	}

	private void addCurrentAttributes(String prefix) {
		if (currentAttributes.size() > 0) {
			final Iterator keys = currentAttributes.keySet().iterator();
			while (keys.hasNext()) {
				final String key = (String) keys.next();
				final String value = currentAttributes.getProperty(key);
				properties.put(prefix + SEPARATOR + key, value);
			}
			currentAttributes.clear();
		}
	}

	public Properties getProperties() {
		return properties;
	}

	public void reset() {
		properties.clear();
	}

}
