package org.codehaus.nanning.config;

import org.codehaus.nanning.AspectInstance;

public interface Aspect {
    void introduce(AspectInstance aspectInstance);

    void advise(AspectInstance aspectInstance);
}
