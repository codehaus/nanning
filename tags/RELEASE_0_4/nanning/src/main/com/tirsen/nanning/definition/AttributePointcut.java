/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 * (C) 2003 Jon Tirsen
 */
package com.tirsen.nanning.definition;

import com.tirsen.nanning.MixinInstance;
import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.attribute.Attributes;
import com.tirsen.nanning.config.Advise;
import com.tirsen.nanning.config.Pointcut;

import java.lang.reflect.Method;

public class AttributePointcut extends Pointcut {
    private String attribute;

    public AttributePointcut(String attribute) {
        this.attribute = attribute;
    }

    public AttributePointcut(String attribute, Advise advise) {
        this(attribute);
        addAdvise(advise);
    }

    protected boolean adviseInstance(AspectInstance aspectInstance) {
        if (aspectInstance.getClassIdentifier() instanceof Class) {
            return Attributes.hasInheritedAttribute((Class) aspectInstance.getClassIdentifier(), attribute);
        }
        return super.adviseInstance(aspectInstance);
    }

    protected boolean adviseMixin(MixinInstance mixinInstance) {
        return Attributes.hasInheritedAttribute(mixinInstance.getInterfaceClass(), attribute);
    }

    protected boolean adviseMethod(MixinInstance mixinInstance, Method method) {
        return Attributes.hasAttribute(method, attribute);
    }
}
