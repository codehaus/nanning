package org.codehaus.nanning.xml;

import org.codehaus.nanning.AspectException;
import org.codehaus.nanning.config.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class AspectSystemParser {
    public AspectSystem parse(InputStream input) throws IOException {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(input);

            AspectSystem aspectSystem = new AspectSystem();

            parseAspectSystem(document.getDocumentElement(), aspectSystem);

            return aspectSystem;
        } catch (ParserConfigurationException e) {
            throw new AspectException(e);
        } catch (SAXException e) {
            throw new AspectException(e);
        } catch (IOException e) {
            throw new AspectException(e);
        }
    }

    private void parseAspectSystem(Element element, AspectSystem aspectSystem) {
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if ("aspect".equals(node.getNodeName())) {
                aspectSystem.addAspect(parseAspect((Element) node));
            } else if ("interceptor".equals(node.getNodeName())) {
                aspectSystem.addAspect(parseInterceptorAspect((Element) node));
            } else if ("class".equals(node.getNodeName())) {
                aspectSystem.addAspect(parseClass((Element) node));
            } else if ("mixin".equals(node.getNodeName())) {
                aspectSystem.addAspect(parseMixinAspect((Element) node));
            }
        }
    }

    private MixinAspect parseMixinAspect(Element element) {
        return new MixinAspect(loadClass(element.getAttribute("interface")), loadClass(element.getAttribute("target")));
    }

    private ClassAspect parseClass(Element element) {
        ClassAspect classAspect = new ClassAspect(loadClass(element.getAttribute("name")));
        parseAspectSystem(element, classAspect);
        return classAspect;
    }

    private Aspect parseInterceptorAspect(Element element) {
        Class interceptorClass = loadClass(element.getAttribute("class"));

        if (element.getElementsByTagName("pointcut").getLength() == 0) {
            return new InterceptorAspect(interceptorClass, InterceptorAspect.PER_METHOD);
        } else {
            Element pointcutElement = (Element) element.getElementsByTagName("pointcut").item(0);
            Pointcut pointcut = parsePointcut(pointcutElement);
            return new InterceptorAspect(pointcut, interceptorClass,
                    InterceptorAspect.PER_METHOD);
        }
    }

    private Pointcut parsePointcut(Element pointcutElement) {
        return P.methodAttribute(pointcutElement.getAttribute("attribute"));
    }

    private Aspect parseAspect(Element element) {
        return (Aspect) newInstance(element.getAttribute("class"));
    }

    private Class loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new AspectException(e);
        }
    }

    private Object newInstance(String className) {
        try {
            Class aspectClass = Class.forName(className);
            return aspectClass.newInstance();
        } catch (Exception e) {
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
