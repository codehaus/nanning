package com.tirsen.nanning.xml;

import com.tirsen.nanning.config.InterceptorAspect;
import com.tirsen.nanning.config.Pointcut;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.ObjectCreationFactory;
import org.xml.sax.Attributes;

public class InterceptorAspectFactory implements ObjectCreationFactory {
    public Object createObject(Attributes attributes) throws Exception {
        Class interceptorClass = Class.forName(attributes.getValue("class"));
        return new InterceptorAspect(interceptorClass, InterceptorAspect.PER_METHOD);
    }

    public Digester getDigester() {
        return null;
    }

    public void setDigester(Digester digester) {
    }
}
