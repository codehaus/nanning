package com.tirsen.nanning.config;

import com.tirsen.nanning.MixinInstance;

import java.lang.reflect.Method;

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
