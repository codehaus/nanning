package org.codehaus.nanning.config;

import org.codehaus.nanning.AspectException;
import org.codehaus.nanning.AspectInstance;
import org.codehaus.nanning.Mixin;

public class MixinAspect extends AbstractAspect {
    protected Class interfaceClass;
    protected Class targetClass;
    protected Pointcut pointcut;

    public MixinAspect(Class interfaceClass, Class targetClass, Pointcut pointcut) {
        this.interfaceClass = interfaceClass;
        this.targetClass = targetClass;
        this.pointcut = pointcut;
    }

    public MixinAspect(Class interfaceClass, Class targetClass) {
        this(interfaceClass, targetClass, P.isClass(interfaceClass));
    }

    public void introduce(AspectInstance aspectInstance) {
        if (shouldIntroduce(aspectInstance)) {
            Mixin mixinInstance = new Mixin();
            mixinInstance.setInterfaceClass(interfaceClass);
            if (targetClass != null) {
                try {
                    mixinInstance.setTarget(targetClass.newInstance());
                } catch (Exception e) {
                    throw new AspectException("could not instantiate target", e);
                }
            }
            aspectInstance.addMixin(mixinInstance);
        }
    }

    public boolean shouldIntroduce(AspectInstance aspectInstance) {
        return pointcut.introduceOn(aspectInstance);
    }

    public Class getInterfaceClass() {
        return interfaceClass;
    }

    public Class getTargetClass() {
        return targetClass;
    }
}
