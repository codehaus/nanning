package com.tirsen.nanning.config;

import com.tirsen.nanning.attribute.Attributes;

import java.lang.reflect.Method;

public class AttributePointcut extends AbstractPointcut {
    private String attribute;

    public AttributePointcut(String attribute) {
        this.attribute = attribute;
    }

    protected boolean adviseMethod(Method method) {
        return Attributes.hasAttribute(method, attribute);
    }
}
