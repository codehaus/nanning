package com.tirsen.nanning.config;

import java.lang.reflect.Method;

import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MixinInstance;

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
