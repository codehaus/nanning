package com.tirsen.nanning.config;

import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MixinInstance;

import java.lang.reflect.Method;

/**
 * Advises adds behaviour or state to methods or aspected instances such as adding interceptors or mixins.p
 */
public class Advise {
    public void advise(AspectInstance aspectInstance) {
    }

    public void advise(MixinInstance mixinInstance) {
    }

    public void advise(MixinInstance mixinInstance, Method method) {
    }
}
