package com.tirsen.nanning.attribute;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

public class AttributesXMLParser {
    private String fieldName;
    private String methodName;
    private String argumentList;
    private ClassAttributes classAttributes;

    public AttributesXMLParser(ClassAttributes classAttributes) {
        this.classAttributes = classAttributes;
    }

    public static void parseXML(InputStream input, ClassAttributes classAttributes) throws IOException, SAXException {
        AttributesXMLParser attributesXMLParser = new AttributesXMLParser(classAttributes);
        attributesXMLParser.parse(input);
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
            classAttributes.loadFieldAttribute(fieldName, name, value);
            
        } else if (methodName != null) {
            String methodSignature = methodName + "(" + ((argumentList == null) ? "" : argumentList) + ")";
            classAttributes.loadMethodAttribute(methodSignature, name, value);

        } else {
            classAttributes.loadClassAttribute(name, value);

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
