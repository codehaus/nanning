package com.tirsen.nanning.config;

import com.tirsen.nanning.AspectException;
import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MixinInstance;

public class Introductor implements Aspect {
    private Class interfaceClass;
    private Class targetClass;

    public Introductor(Class interfaceClass, Class targetClass) {
        this.interfaceClass = interfaceClass;
        this.targetClass = targetClass;
    }

    public Object advise(AspectInstance aspectInstance, MixinInstance mixin) {
        return null;
    }

    public Object adviseConstruction(AspectInstance aspectInstance) {
        return null;
    }

    public Object introduce(AspectInstance aspectInstance) {
        MixinInstance mixinInstance = new MixinInstance();
        mixinInstance.setInterfaceClass(interfaceClass);
        if (targetClass != null) {
            try {
                mixinInstance.setTarget(targetClass.newInstance());
            } catch (Exception e) {
                throw new AspectException("could not instantiate target " + e);
            }
        }
        return mixinInstance;
    }
}
