package com.tirsen.nanning.xml;

import com.tirsen.nanning.config.InterceptorAspect;
import com.tirsen.nanning.config.Introductor;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.ObjectCreationFactory;
import org.xml.sax.Attributes;

public class IntroductorAspectFactory implements ObjectCreationFactory {
    public Object createObject(Attributes attributes) throws Exception {
        Class interfaceClass = Class.forName(attributes.getValue("interface"));
        Class targetClass = Class.forName(attributes.getValue("target"));
        return new Introductor(interfaceClass, targetClass);
    }

    public Digester getDigester() {
        return null;
    }

    public void setDigester(Digester digester) {
    }
}
