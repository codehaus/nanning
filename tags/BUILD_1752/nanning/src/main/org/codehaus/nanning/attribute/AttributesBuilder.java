package org.codehaus.nanning.attribute;

import java.util.Iterator;
import java.util.Properties;
import java.util.List;
import java.util.Stack;
import java.util.ArrayList;

import com.thoughtworks.qdox.parser.Builder;
import com.thoughtworks.qdox.parser.structs.ClassDef;
import com.thoughtworks.qdox.parser.structs.FieldDef;
import com.thoughtworks.qdox.parser.structs.MethodDef;

/**
 * QDox Builder implementation for creating Properties containing attributes.
 *
 * <p>This Builder should be fed to the QDox Parser where it shall receive callbacks as a source file is parsed.
 * After the file has been parsed, getEdit() can be called to retrieved the compiled classPropertiesHelper of the class.</p>
 *
 * <p>An AttributesBuilder can only be used to parse <b>one</b> file at a time. If the AttributesBuilder is to be reused
 * to parse another file, the reset() method must be called.</p>
 *
 * @author <a href="joe@truemesh.org">Joe Walnes</a>
 * @version $Revision: 1.1 $
 */
public class AttributesBuilder implements Builder {

    private final Properties currentAttributes = new Properties();
    private Stack classPropertiesHelperStack = new Stack();
    private List classPropertiesHelpers = new ArrayList();
    private String packageName;


    public void addPackage(String packageName) {
        this.packageName = packageName;
    }

    public void addImport(String importName) {
    }

    public void addJavaDoc(String text) {
    }

    public void endClass() {
        classPropertiesHelperStack.pop();
    }

    public void addJavaDocTag(String tag, String text) {
        currentAttributes.setProperty(tag, text);
    }

    public void beginClass(ClassDef def) {
        String baseClassName = "";
        if (!classPropertiesHelperStack.isEmpty()) {
            baseClassName = classPropertiesHelper().getClassName() + "$";
        }

        classPropertiesHelperStack.push(new ClassPropertiesHelper());
        classPropertiesHelpers.add(classPropertiesHelper());

        classPropertiesHelper().setClassName(baseClassName + def.name);

        classPropertiesHelper().setPackageName(packageName);
        addCurrentAttributes(null, null);
    }

    private ClassPropertiesHelper classPropertiesHelper() {
        return (ClassPropertiesHelper) classPropertiesHelperStack.peek();
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

        addCurrentAttributes(null, method.toString());
    }

    private String getTypeWithoutPackage(FieldDef param) {
        String type = param.type;
        if (type.indexOf('.') != -1) {
            type = type.substring(type.lastIndexOf('.') + 1);
        }
        return type;
    }

    public void addField(FieldDef def) {
        addCurrentAttributes(def.name, null);
    }

    private void addCurrentAttributes(String fieldName, String methodSignature) {
        if (currentAttributes.size() > 0) {
            final Iterator keys = currentAttributes.keySet().iterator();
            while (keys.hasNext()) {
                final String attributeName = (String) keys.next();
                final String attributeValue = currentAttributes.getProperty(attributeName);

                if (fieldName != null) {
                    classPropertiesHelper().loadFieldAttribute(fieldName, attributeName, attributeValue);

                } else if (methodSignature != null) {
                    classPropertiesHelper().loadMethodAttribute(methodSignature, attributeName, attributeValue);

                } else {
                    classPropertiesHelper().loadClassAttribute(attributeName, attributeValue);

                }
            }
            currentAttributes.clear();
        }
    }

    public List getClassPropertiesHelpers() {
        assert classPropertiesHelperStack.isEmpty();
        return classPropertiesHelpers;
    }
}