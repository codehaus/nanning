package com.tirsen.nanning.attribute;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class AttributesXMLParser {
    private Properties properties;
    private String fieldName;
    private String methodName;
    private String argumentList;

    protected AttributesXMLParser() {
        properties = new Properties();
    }

    public static Properties parseXML(InputStream input) throws IOException, SAXException {
        AttributesXMLParser attributesXMLParser = new AttributesXMLParser();
        attributesXMLParser.parse(input);
        return attributesXMLParser.getProperties();
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
        String propertyName = null;
        if (fieldName != null) {
            propertyName = fieldName + "." + name;
        } else if (methodName != null) {
            propertyName = methodName + "(" + ((argumentList == null) ? "" : argumentList) + ")" + "." + name;
        } else {
            propertyName = "class." + name;
        }
        properties.setProperty(propertyName, value);
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
        if(argumentList == null) {
            argumentList = type;
        } else {
            argumentList += ",";
            argumentList += type;
        }
    }

    private Properties getProperties() {
        return properties;
    }

}
