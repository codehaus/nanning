package com.tirsen.nanning.config;

import java.lang.reflect.Method;

import com.tirsen.nanning.MixinInstance;

/**
 * Advises all methods but nothing else.
 */
public class MethodPointcut extends Pointcut {
    public MethodPointcut() {
    }

    public MethodPointcut(Advise advise) {
        super(advise);
    }

    protected boolean adviseMethod(MixinInstance mixinInstance, Method method) {
        return true;
    }
}
