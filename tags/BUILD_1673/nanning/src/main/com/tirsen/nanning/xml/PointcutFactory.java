package com.tirsen.nanning.xml;

import com.tirsen.nanning.config.InterceptorAspect;
import com.tirsen.nanning.config.AttributePointcut;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.ObjectCreationFactory;
import org.xml.sax.Attributes;

public class PointcutFactory implements ObjectCreationFactory {
    public Object createObject(Attributes attributes) throws Exception {
        return new AttributePointcut(attributes.getValue("attribute"));
    }

    public Digester getDigester() {
        return null;
    }

    public void setDigester(Digester digester) {
    }
}
