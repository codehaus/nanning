package org.codehaus.nanning.config;

import java.util.List;

import org.codehaus.nanning.AspectInstance;
import org.codehaus.nanning.Mixin;

public interface Aspect {
    void introduce(AspectInstance aspectInstance);

    void advise(AspectInstance aspectInstance);
}
