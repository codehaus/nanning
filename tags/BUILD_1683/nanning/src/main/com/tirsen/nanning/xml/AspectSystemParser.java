package com.tirsen.nanning.xml;

import com.tirsen.nanning.AspectException;
import com.tirsen.nanning.config.AspectSystem;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.apache.commons.digester.xmlrules.DigesterLoadingException;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.net.URL;

public class AspectSystemParser {
    private static final String RULES_RESOURCE = "com/tirsen/nanning/xml/rules.xml";

    public AspectSystem parse(InputStream input) throws IOException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader classLoader = getClass().getClassLoader();
        URL rules = null;
        if (contextClassLoader != null) {
            rules = contextClassLoader.getResource(RULES_RESOURCE);
        }
        if (rules == null) {
            rules = classLoader.getResource(RULES_RESOURCE);
        }
        assert rules != null : "could not find rules in class-loader " + classLoader;
        try {
            return (AspectSystem) DigesterLoader.load(rules,
                    contextClassLoader != null ? contextClassLoader : classLoader, input);
        } catch (SAXException e) {
            throw new AspectException(e);
        } catch (DigesterLoadingException e) {
            throw new AspectException(e);
        }
    }

    public AspectSystem parse(String xmlAsString) throws IOException {
        return parse(new ByteArrayInputStream(xmlAsString.getBytes()));
    }

    public AspectSystem parse(URL resource) throws IOException {
        InputStream input = resource.openStream();
        try {
            return parse(input);
        } finally {
            input.close();
        }
    }
}
