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
 * After the file has been parsed, getEdit() can be called to retrieved the compiled classAttributes of the class.</p>
 *
 * <p>An AttributesBuilder can only be used to parse <b>one</b> file at a time. If the AttributesBuilder is to be reused
 * to parse another file, the reset() method must be called.</p>
 *
 * @author <a href="joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.8 $
 */
public class AttributesBuilder implements Builder {

    private ClassAttributes classAttributes = new ClassAttributes();

    private final Properties currentAttributes = new Properties();


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
        addCurrentAttributes(true, null, null);
    }

    public void addMethod(MethodDef def) {
        // don't build attributes for constructors as it is not supported
        if (def.constructor) {
            return;
        }

        final StringBuffer method = new StringBuffer();
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

        addCurrentAttributes(false, null, method.toString());
    }

    private String getTypeWithoutPackage(FieldDef param) {
        String type = param.type;
        if (type.indexOf('.') != -1) {
            type = type.substring(type.lastIndexOf('.') + 1);
        }
        return type;
    }

    public void addField(FieldDef def) {
        addCurrentAttributes(false, def.name, null);
    }

    private void addCurrentAttributes(boolean isClass, String fieldName, String methodSignature) {
        if (currentAttributes.size() > 0) {
            final Iterator keys = currentAttributes.keySet().iterator();
            while (keys.hasNext()) {
                final String attributeName = (String) keys.next();
                final String attributeValue = currentAttributes.getProperty(attributeName);

                if (isClass) {
                    classAttributes.loadClassAttribute(attributeName, attributeValue);

                } else if (fieldName != null) {
                    classAttributes.loadFieldAttribute(fieldName, attributeName, attributeValue);

                } else if (methodSignature != null) {
                    classAttributes.loadMethodAttribute(methodSignature, attributeName, attributeValue);

                }
            }
            currentAttributes.clear();
        }
    }

    public ClassAttributes getClassAttributes() {
        return classAttributes;
    }

    public void reset() {
        classAttributes = new ClassAttributes();
    }

}