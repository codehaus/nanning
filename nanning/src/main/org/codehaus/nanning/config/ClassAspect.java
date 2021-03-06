package org.codehaus.nanning.config;

import org.codehaus.nanning.AspectInstance;

public class ClassAspect extends AspectSystem implements Aspect {
    private Class classIdentifier;

    public ClassAspect(Class classIdentifier) {
        this.classIdentifier = classIdentifier;
    }

    public void introduce(AspectInstance aspectInstance) {
        if (shouldConfigure(aspectInstance)) {
            super.introduce(aspectInstance);
        }
    }

    private boolean shouldConfigure(AspectInstance aspectInstance) {
        return classIdentifier.equals(aspectInstance.getClassIdentifier());
    }

    public void advise(AspectInstance aspectInstance) {
        if (shouldConfigure(aspectInstance)) {
            super.advise(aspectInstance);
        }
    }

    public Class getClassIdentifier() {
        return classIdentifier;
    }
}
