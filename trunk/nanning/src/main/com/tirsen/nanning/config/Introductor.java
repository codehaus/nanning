package com.tirsen.nanning.config;

import com.tirsen.nanning.AspectException;
import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MixinInstance;

public class Introductor extends AbstractAspect {
    protected Class interfaceClass;
    protected Class targetClass;

    public Introductor(Class interfaceClass, Class targetClass) {
        this.interfaceClass = interfaceClass;
        this.targetClass = targetClass;
    }

    public void introduce(AspectInstance aspectInstance) {
        if (shouldIntroduce(aspectInstance)) {
            MixinInstance mixinInstance = new MixinInstance();
            mixinInstance.setInterfaceClass(interfaceClass);
            if (targetClass != null) {
                try {
                    mixinInstance.setTarget(targetClass.newInstance());
                } catch (Exception e) {
                    throw new AspectException("could not instantiate target " + e);
                }
            }
            aspectInstance.addMixin(mixinInstance);
        }
    }

    public boolean shouldIntroduce(AspectInstance aspectInstance) {
        return true;
    }

    public Class getInterfaceClass() {
        return interfaceClass;
    }

    public Class getTargetClass() {
        return targetClass;
    }
}
