package com.tirsen.nanning.config;

import java.util.List;

import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MixinInstance;

public interface Aspect {
    void introduce(AspectInstance aspectInstance);

    void advise(AspectInstance aspectInstance);
}
