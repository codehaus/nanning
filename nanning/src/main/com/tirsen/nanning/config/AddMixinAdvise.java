package com.tirsen.nanning.config;

import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MixinInstance;
import com.tirsen.nanning.AspectException;

public class AddMixinAdvise extends Advise {
    private Class interfaceClass;
    private Class targetClass;

    public AddMixinAdvise(Class interfaceClass, Class targetClass) {
        this.interfaceClass = interfaceClass;
        this.targetClass = targetClass;
    }

    public void advise(AspectInstance aspectInstance) {
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
