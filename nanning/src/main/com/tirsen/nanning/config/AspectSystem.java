package com.tirsen.nanning.config;

import java.util.*;

import com.tirsen.nanning.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

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
     * Let the aspect advice and introduce.
     * @param aspectInstance
     */
    public void initialize(AspectInstance aspectInstance) {
        introduce(aspectInstance);
        advice(aspectInstance);
    }

    protected void advice(AspectInstance aspectInstance) {
        for (Iterator aspectIterator = aspects.iterator(); aspectIterator.hasNext();) {
            Aspect aspect = (Aspect) aspectIterator.next();

            aspect.advise(aspectInstance);
        }
        adviceMixins(aspectInstance);
    }

    protected void adviceMixins(AspectInstance aspectInstance) {
        for (Iterator mixinIterator = aspectInstance.getMixins().iterator(); mixinIterator.hasNext();) {
            MixinInstance mixinInstance = (MixinInstance) mixinIterator.next();

            for (Iterator aspectIterator = aspects.iterator(); aspectIterator.hasNext();) {
                Aspect aspect = (Aspect) aspectIterator.next();
                aspect.adviseMixin(aspectInstance, mixinInstance);
            }
        }
    }

    protected void introduce(AspectInstance aspectInstance) {
        for (Iterator iterator = aspects.iterator(); iterator.hasNext();) {
            Aspect aspect = (Aspect) iterator.next();
            aspect.introduce(aspectInstance);
        }
    }

    /**
     * Called after serialization, just advice.
     * @param aspectInstance
     */
    public void reinitialize(AspectInstance aspectInstance) {
        advice(aspectInstance);
    }

    public List getAspects() {
        return Collections.unmodifiableList(aspects);
    }
}
