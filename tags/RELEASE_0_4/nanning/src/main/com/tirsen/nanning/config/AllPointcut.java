package com.tirsen.nanning.config;

import java.lang.reflect.Method;

import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MixinInstance;

public class AllPointcut extends Pointcut {
    public AllPointcut() {
    }

    public AllPointcut(Advise advise) {
        super(advise);
    }

    protected boolean adviseInstance(AspectInstance aspectInstance) {
        return true;
    }

    protected boolean adviseMixin(MixinInstance mixinInstance) {
        return true;
    }

    protected boolean adviseMethod(MixinInstance mixinInstance, Method method) {
        return true;
    }
}
