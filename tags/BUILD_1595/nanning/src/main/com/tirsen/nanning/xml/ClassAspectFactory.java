package com.tirsen.nanning.xml;

import com.tirsen.nanning.config.ClassAspect;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.ObjectCreationFactory;
import org.xml.sax.Attributes;

public class ClassAspectFactory implements ObjectCreationFactory {
    public Object createObject(Attributes attributes) throws Exception {
        Class classIdentifier = Class.forName(attributes.getValue("name"));
        return new ClassAspect(classIdentifier);
    }

    public Digester getDigester() {
        return null;
    }

    public void setDigester(Digester digester) {
    }
}
