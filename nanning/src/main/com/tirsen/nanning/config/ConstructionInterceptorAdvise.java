package com.tirsen.nanning.config;

import com.tirsen.nanning.ConstructionInterceptor;
import com.tirsen.nanning.AspectInstance;

public class ConstructionInterceptorAdvise extends Advise {
    private ConstructionInterceptor singletonInterceptor;

    public ConstructionInterceptorAdvise(Class interceptorClass) {
        assert false : "not implemented yet";
    }

    public ConstructionInterceptorAdvise(ConstructionInterceptor constructionInterceptor) {
        this.singletonInterceptor = constructionInterceptor;
    }

    public void advise(AspectInstance aspectInstance) {
        aspectInstance.addConstructionInterceptor(singletonInterceptor);
    }
}
