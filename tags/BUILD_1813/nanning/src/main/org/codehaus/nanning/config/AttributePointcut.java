package org.codehaus.nanning.config;

import org.codehaus.nanning.attribute.Attributes;
import org.codehaus.nanning.AspectInstance;
import org.codehaus.nanning.Mixin;

import java.lang.reflect.Method;

public class AttributePointcut extends Pointcut {
    private String attribute;

    public AttributePointcut(String attribute) {
        this.attribute = attribute;
    }

    public String getAttribute() {
        return attribute;
    }

    public boolean adviseMethod(AspectInstance instance, Mixin mixin, Method method) {
        return Attributes.hasAttribute(method, attribute);
    }
}
