package org.codehaus.nanning.config;

import org.codehaus.nanning.AspectInstance;


public class ClassIntroductor extends Introductor {

    public ClassIntroductor(Class classIdentifier, Class targetClass) {
        super(classIdentifier, targetClass);
    }

    public boolean shouldIntroduce(AspectInstance aspectInstance) {
        return interfaceClass.equals(aspectInstance.getClassIdentifier());
    }
}
