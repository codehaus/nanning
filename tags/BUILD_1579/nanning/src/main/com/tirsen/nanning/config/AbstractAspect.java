package com.tirsen.nanning.config;

import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MixinInstance;

public abstract class AbstractAspect implements Aspect {
    public void advise(AspectInstance aspectInstance) {
    }

    public void adviseMixin(AspectInstance aspectInstance, MixinInstance mixin) {
    }

    public void introduce(AspectInstance aspectInstance) {
    }
}
