package com.tirsen.nanning.config;

import com.tirsen.nanning.AspectInstance;


public class ClassIntroductor extends Introductor {

    public ClassIntroductor(Class classIdentifier, Class targetClass) {
        super(classIdentifier, targetClass);
    }

    public boolean shouldIntroduce(AspectInstance aspectInstance) {
        return interfaceClass.equals(aspectInstance.getClassIdentifier());
    }
}
