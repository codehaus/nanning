package com.tirsen.nanning.attribute;

/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * <!-- $Id: AttributesTagHandler.java,v 1.3 2003-01-24 13:29:30 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.3 $
 *
 */
public class AttributesTagHandler extends DefaultHandler {
    //--------CONSTANTS----------------------------------
    private static final String TAG_CLASS = "class";
    private static final String TAG_FIELD = "field";
    private static final String TAG_METHOD = "method";
    private static final String TAG_ARG = "arg";
    private static final String TAG_TYPE = "type";
    private static final String TAG_ATTRIBUTE = "attribute";
    private static final String TAG_NAME = "name";
    private static final String TAG_VALUE = "value";
    private static final String COMMA = ",";
    private static final String OPEN_P = "(";
    private static final String CLOSE_P = ")";
    private static final String DOT = ".";

    //-----------INSTANCE FIELDS---------------------------
    private StringBuffer textData = new StringBuffer(4096);
    private List argsList;
    private Stack tagStack = new Stack();
    private String currentNameTag;
    private String currentValueTag;
    private String currentTypeTag;
    private String currentMethodName;
    private String currentFieldName;
    private StringBuffer currentPrefix;
    private Map attributeMaps;
    private Properties classAttribs = new Properties();


    //------------ Handle Tags-----------------

    //------------ Start Document -----------------
    public void startDocument() {
        attributeMaps = new HashMap();
    }

    //------------ End Document -----------------
    public void endDocument() {

    }

    //-----Get the text
    public void characters(char ch[], int start, int length) throws org.xml.sax.SAXException {
        super.characters(ch, start, length);

        String s = new String(ch, start, length);
        if (ch[0] == '\n')
            return;
        else if (ch[0] == '\r') return;

        textData.append(s);
    }

    //------------ Start Element-----------------
    /**
     * Handles opening tags...
     * @param uri
     * @param name
     * @param qName
     * @param atts
     */
    public void startElement(String uri, String name,
                             String qName, Attributes atts) {
        if (TAG_FIELD.equals(qName)) {
            currentFieldName = atts.getValue(TAG_NAME);
            tagStack.push(TAG_FIELD);
        } else if (TAG_METHOD.equals(qName)) {
            currentMethodName = atts.getValue(TAG_NAME);
            tagStack.push(TAG_METHOD);

            argsList = new ArrayList();
        } else if (TAG_ATTRIBUTE.equals(qName)) {
            /*  At this point either:
                    we are in 'class' scope (stack is empty) -do nothing
                    we are in 'field' scope -do nothing
                    we are in 'method' scope -We now have all method arguments in list
                                              and can properly format the currentMethodName.
            */

            if (!tagStack.isEmpty() && TAG_METHOD.equals(tagStack.peek())) {
                currentMethodName = formatMethodName();
            }
        }

    }

    //------------ End Element -----------------
    public void endElement(String uri, String name, String qName) {
        if (TAG_ARG.equals(qName)) {
            argsList.add(currentTypeTag);
        } else if (TAG_TYPE.equals(qName)) {
            currentTypeTag = textData.toString();
            currentTypeTag = currentTypeTag.trim();
            textData = new StringBuffer(4096);
        } else if (TAG_ATTRIBUTE.equals(qName)) {
            if (TAG_FIELD.equals(tagStack.peek())) {
                currentPrefix = new StringBuffer();
                currentPrefix.append(currentFieldName);
            } else if (TAG_METHOD.equals(tagStack.peek())) {
                currentPrefix = new StringBuffer();
                currentPrefix.append(currentMethodName);
            }

            currentPrefix.append(DOT);
            currentPrefix.append(currentNameTag);

            classAttribs.put(currentPrefix.toString(), currentValueTag);
        } else if (TAG_NAME.equals(qName)) {
            currentNameTag = textData.toString();
            currentNameTag = currentNameTag.trim();
            textData = new StringBuffer(4096);
        } else if (TAG_VALUE.equals(qName)) {
            currentValueTag = textData.toString();
            currentValueTag = currentValueTag.trim();
            textData = new StringBuffer(4096);
        }


    }

    private String formatMethodName() {
        StringBuffer sb = new StringBuffer();
        sb.append(currentMethodName).append(OPEN_P);

        for (int i = 0; i < argsList.size(); i++) {
            String argType = (String) argsList.get(i);
            sb.append(argType);

            if (i + 1 < argsList.size()) sb.append(COMMA);
        }

        sb.append(CLOSE_P);

        return sb.toString();
    }

    public Map getAttributeMaps() {
        return attributeMaps;
    }

    static Properties parseXML(InputStream inputStream) throws Exception {
        SAXParser parser;

        try {

            parser = SAXParserFactory.newInstance().newSAXParser();

            AttributesTagHandler handler = new AttributesTagHandler();
            parser.parse(inputStream, handler);

            return handler.classAttribs;

        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        } catch (ParserConfigurationException pce) {
            throw new RuntimeException(pce);
        } catch (SAXException saxe) {
            throw new RuntimeException(saxe);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            inputStream.close();
        }
    }
}
