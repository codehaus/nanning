package com.tirsen.nanning.attribute;

import java.util.Iterator;
import java.util.Properties;

import com.thoughtworks.qdox.parser.Builder;
import com.thoughtworks.qdox.parser.structs.ClassDef;
import com.thoughtworks.qdox.parser.structs.FieldDef;
import com.thoughtworks.qdox.parser.structs.MethodDef;

/**
 * QDox Builder implementation for creating Properties containing attributes.
 *
 * <p>This Builder should be fed to the QDox Parser where it shall receive callbacks as a source file is parsed.
 * After the file has been parsed, getEdit() can be called to retrieved the compiled properties of the class.</p>
 *
 * <p>An AttributesBuilder can only be used to parse <b>one</b> file at a time. If the AttributesBuilder is to be reused
 * to parse another file, the reset() method must be called.</p>
 *
 * @author <a href="joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.6 $
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
        final StringBuffer method = new StringBuffer();
        method.append("method.");
        method.append(def.name);
        method.append('(');
        for (Iterator params = def.params.iterator(); params.hasNext();) {
            FieldDef param = (FieldDef) params.next();
            method.append(getTypeWithoutPackage(param));
            method.append(',');
        }
        if (def.params.size() > 0) {
            // trim last comma
            method.setLength(method.length() - 1);
        }
        method.append(')');
        addCurrentAttributes(method.toString());
    }

    private String getTypeWithoutPackage(FieldDef param) {
        String type = param.type;
        if (type.indexOf('.') != -1) {
            type = type.substring(type.lastIndexOf('.') + 1);
        }
        return type;
    }

    public void addField(FieldDef def) {
        addCurrentAttributes("field." + def.name);
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