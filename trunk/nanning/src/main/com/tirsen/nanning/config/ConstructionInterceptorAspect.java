package com.tirsen.nanning.config;

import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.ConstructionInterceptor;
import com.tirsen.nanning.MixinInstance;

import java.util.Collection;
import java.util.Iterator;

public class ConstructionInterceptorAspect implements Aspect {
    private Class interceptorClass;

    public ConstructionInterceptorAspect(Class interceptorClass) {
        this.interceptorClass = interceptorClass;
    }

    public void process(AspectInstance aspectInstance) {
        try {
            ConstructionInterceptor constructionInterceptor = (ConstructionInterceptor) interceptorClass.newInstance();
            Collection mixins = aspectInstance.getMixins();
            for (Iterator iterator = mixins.iterator(); iterator.hasNext();) {
                MixinInstance mixinInstance = (MixinInstance) iterator.next();
                if (constructionInterceptor.interceptsConstructor(mixinInstance.getInterfaceClass())) {
                    aspectInstance.addConstructionInterceptor(constructionInterceptor);
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("failed to instantiate interceptor", e);
        }
    }
}
