package com.tirsen.nanning.config;

import java.util.List;

import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MixinInstance;

public interface Aspect {

    /**
     * @return a single or a collection of mixins
     */
    Object introduce(AspectInstance aspectInstance);

    /** 
     * @return a single or a collection of interceptors
     */
    Object advise(AspectInstance aspectInstance, MixinInstance mixin);

    /**
     * @return a single or a collection of construction interceptors
     */
    Object adviseConstruction(AspectInstance aspectInstance);
}
