package com.tirsen.nanning.config;

import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.attribute.Attributes;

public class AttributeIntroductor extends Introductor {
    private String attribute;

    public AttributeIntroductor(Class interfaceClass, Class targetClass, String attribute) {
        super(interfaceClass, targetClass);
        this.attribute = attribute;
    }

    public void introduce(AspectInstance aspectInstance) {
        if (Attributes.hasInheritedAttribute(aspectInstance.getClassIdentifier(), attribute)) {
            super.introduce(aspectInstance);
        }
    }
}
