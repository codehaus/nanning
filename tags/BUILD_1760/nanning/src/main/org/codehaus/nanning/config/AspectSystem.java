package org.codehaus.nanning.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.codehaus.nanning.AspectFactory;
import org.codehaus.nanning.AspectInstance;

public class AspectSystem implements AspectFactory {
    private List aspects = new ArrayList();

    public void addAspect(Aspect aspect) {
        aspects.add(aspect);
    }

    public Object newInstance(Class classIdentifier) {
        AspectInstance aspectInstance = createAspectInstance(classIdentifier);
        return aspectInstance.getProxy();
    }

    private AspectInstance createAspectInstance(Class classIdentifier) {
        AspectInstance aspectInstance = new AspectInstance(this, classIdentifier);

        initialize(aspectInstance);

        return aspectInstance;
    }

    /**
     * Let the aspects advise and introduce.
     * @param aspectInstance
     */
    public void initialize(AspectInstance aspectInstance) {
        introduce(aspectInstance);
        advise(aspectInstance);
    }

    protected void advise(AspectInstance aspectInstance) {
        for (Iterator aspectIterator = aspects.iterator(); aspectIterator.hasNext();) {
            Aspect aspect = (Aspect) aspectIterator.next();

            aspect.advise(aspectInstance);
        }
    }

    protected void introduce(AspectInstance aspectInstance) {
        for (Iterator iterator = aspects.iterator(); iterator.hasNext();) {
            Aspect aspect = (Aspect) iterator.next();
            aspect.introduce(aspectInstance);
        }
    }

    /**
     * Called after serialization, just advise.
     * @param aspectInstance
     */
    public void reinitialize(AspectInstance aspectInstance) {
        advise(aspectInstance);
    }

    public List getAspects() {
        return Collections.unmodifiableList(aspects);
    }
}
