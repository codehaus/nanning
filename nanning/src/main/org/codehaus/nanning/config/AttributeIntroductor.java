package org.codehaus.nanning.config;

import org.codehaus.nanning.AspectInstance;
import org.codehaus.nanning.attribute.Attributes;

public class AttributeIntroductor extends Introductor {
    private String attribute;

    public AttributeIntroductor(Class interfaceClass, Class targetClass, String attribute) {
        super(interfaceClass, targetClass);
        this.attribute = attribute;
    }

    public boolean shouldIntroduce(AspectInstance aspectInstance) {
        return Attributes.hasInheritedAttribute(aspectInstance.getClassIdentifier(), attribute);
    }
}
