package org.codehaus.nanning.attribute;

import org.xml.sax.SAXException;
import org.apache.commons.digester.Digester;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

public class AttributesXMLParser implements AttributesLoader {
    private String fieldName;
    private String methodName;
    private String argumentList;
    private ClassPropertiesHelper classPropertiesHelper;

    public void load(ClassAttributes classAttributes) {
        this.classPropertiesHelper = new ClassPropertiesHelper(classAttributes);
        Class aClass = classAttributes.getAttributeClass();
        InputStream input = null;
        try {
            // load the XML-defined attributes
            input = Attributes.findFile(aClass, classPropertiesHelper.getClassName() + ".xml");
            if (input == null) {
                input = Attributes.findFile(aClass, aClass.getName().replace('.', '/') + ".xml");
            }

            if (input != null) {
                parse(input);
            }

        } catch (MalformedURLException e) {
            throw new AttributeException("Error fetching properties for " + aClass, e);
        } catch (IOException e) {
            throw new AttributeException("Error fetching properties for " + aClass, e);
        } catch (AttributeException e) {
            throw e;
        } catch (Exception e) {
            throw new AttributeException("Error fetching properties for " + aClass, e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    throw new AttributeException(e);
                }
            }
        }
    }

    private void parse(InputStream input) throws IOException, SAXException {
        Digester digester = new Digester();

        digester.addCallMethod("*/attribute", "setAttributeValue", 2);
        digester.addCallParam("*/attribute/name", 0);
        digester.addCallParam("*/attribute/value", 1);

        digester.addSetProperties("attributes/method", "name", "methodName");
        digester.addCallMethod("attributes/method/parameter-type", "addArgumentType", 0);

        digester.addSetProperties("attributes/field", "name", "fieldName");

        digester.push(this);
        digester.parse(input);
    }

    public void setAttributeValue(String name, String value) {
        if (fieldName != null) {
            classPropertiesHelper.loadFieldAttribute(fieldName, name, value);

        } else if (methodName != null) {
            String methodSignature = methodName + "(" + ((argumentList == null) ? "" : argumentList) + ")";
            classPropertiesHelper.loadMethodAttribute(methodSignature, name, value);

        } else {
            classPropertiesHelper.loadClassAttribute(name, value);

        }
        fieldName = null;
        methodName = null;
        argumentList = null;
    }

    public void setFieldName(String name) {
        fieldName = name;
    }

    public void setMethodName(String name) {
        methodName = name;
    }

    public void addArgumentType(String type) {
        if (argumentList == null) {
            argumentList = type;
        } else {
            argumentList += ",";
            argumentList += type;
        }
    }
}
