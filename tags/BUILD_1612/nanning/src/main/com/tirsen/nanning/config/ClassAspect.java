package com.tirsen.nanning.config;

import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MixinInstance;

public class ClassAspect extends AspectSystem implements Aspect {
    private Class classIdentifier;

    public ClassAspect(Class classIdentifier) {
        this.classIdentifier = classIdentifier;
    }

    public void introduce(AspectInstance aspectInstance) {
        if (shouldConfigure(aspectInstance)) {
            super.introduce(aspectInstance);
        }
    }

    private boolean shouldConfigure(AspectInstance aspectInstance) {
        return classIdentifier.equals(aspectInstance.getClassIdentifier());
    }

    public void adviseMixin(AspectInstance aspectInstance, MixinInstance mixin) {
        if (shouldConfigure(aspectInstance)) {
            adviceMixins(aspectInstance);
        }
    }

    public void advise(AspectInstance aspectInstance) {
        if (shouldConfigure(aspectInstance)) {
            super.advice(aspectInstance);
        }
    }

    public Class getClassIdentifier() {
        return classIdentifier;
    }
}
