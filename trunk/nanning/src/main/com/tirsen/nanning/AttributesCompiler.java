/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * TODO document AttributesCompiler
 *
 * <!-- $Id: AttributesCompiler.java,v 1.1 2002-10-28 21:45:34 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public class AttributesCompiler
{
    private File src;
    private File dest;

    public void setSrc(File src)
    {
        this.src = src;
    }

    public void setDest(File dest)
    {
        this.dest = dest;
    }

    public void execute() throws IOException
    {
        System.out.println("Compiling attributes for " + src + " into " + dest);
        JavaDocBuilder javaDocBuilder = new JavaDocBuilder();
        javaDocBuilder.addSourceTree(src);
        JavaSource[] sources = javaDocBuilder.getSources();
        for (int sourceIndex = 0; sourceIndex < sources.length; sourceIndex++)
        {
            JavaSource source = sources[sourceIndex];
            JavaClass[] classes = source.getClasses();
            for (int classIndex = 0; classIndex < classes.length; classIndex++)
            {
                JavaClass javaClass = classes[classIndex];
                JavaField[] fields = javaClass.getFields();
                Properties properties = new Properties();
                processTags("class", javaClass.getTags(), properties);
                for (int fieldIndex = 0; fieldIndex < fields.length; fieldIndex++)
                {
                    JavaField field = fields[fieldIndex];
                    processTags(field.getName(), field.getTags(), properties);
                }
                JavaMethod[] methods = javaClass.getMethods();
                for (int methodIndex = 0; methodIndex < methods.length; methodIndex++)
                {
                    JavaMethod method = methods[methodIndex];
                    StringBuffer name = new StringBuffer();
                    name.append(method.getName());
                    name.append('(');
                    JavaParameter[] parameters = method.getParameters();
                    for (int parameterIndex = 0; parameterIndex < parameters.length; parameterIndex++)
                    {
                        JavaParameter parameter = parameters[parameterIndex];
                        name.append(parameter.getType().getValue());
                        if(parameterIndex + 1 < parameters.length)
                        {
                            name.append(',');
                        }
                    }
                    name.append(')');
                    processTags(name.toString(), method.getTags(), properties);
                }
                File dir = new File(dest, javaClass.getPackage().replace('.', File.separatorChar));
                dir.mkdirs();
                File attributeFile = new File(dir, javaClass.getName() + ".attributes");
                OutputStream output = null;
                try
                {
                    output = new FileOutputStream(attributeFile);
                    properties.store(output, javaClass.getName());
                }
                finally
                {
                    if (output != null)
                    {
                        output.close();
                    }
                }
            }
        }
    }

    private void processTags(String prefix, DocletTag[] tags, Properties properties)
    {
        for (int i = 0; i < tags.length; i++)
        {
            DocletTag tag = tags[i];
            properties.put(prefix + '.' + tag.getName(), tag.getValue());
        }
    }

}
