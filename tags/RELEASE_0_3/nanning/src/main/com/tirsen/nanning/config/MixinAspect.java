package com.tirsen.nanning.config;

import com.tirsen.nanning.AspectInstance;
import org.apache.commons.lang.builder.ToStringBuilder;

public class MixinAspect extends PointcutAspect {
    private Class interfaceClass;
    private Class targetClass;

    public MixinAspect(final Class interfaceClass, Class targetClass) {
        this.interfaceClass = interfaceClass;
        this.targetClass = targetClass;
        addPointcut(new Pointcut(new AddMixinAdvise(interfaceClass, targetClass)) {
            protected boolean adviseInstance(AspectInstance aspectInstance) {
                return aspectInstance.getClassIdentifier().equals(interfaceClass);
            }
        });
    }

    public String toString() {
        return new ToStringBuilder(this).append("interface", interfaceClass).append("target", targetClass).toString();
    }
}
