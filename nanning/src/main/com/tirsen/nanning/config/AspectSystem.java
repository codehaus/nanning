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
        return aspectInstance.getProxy(true);
    }

    private AspectInstance createAspectInstance(Class classIdentifier) {
        AspectInstance aspectInstance = new AspectInstance(this, classIdentifier);

        introduceMixins(aspectInstance);
        adviceConstruction(aspectInstance);
        advice(aspectInstance);

        return aspectInstance;
    }

    private void adviceConstruction(AspectInstance aspectInstance) {
        for (Iterator aspectIterator = aspects.iterator(); aspectIterator.hasNext();) {
            Aspect aspect = (Aspect) aspectIterator.next();

            aspect.advise(aspectInstance);
        }
    }

    private void advice(AspectInstance aspectInstance) {
        for (Iterator mixinIterator = aspectInstance.getMixins().iterator(); mixinIterator.hasNext();) {
            MixinInstance mixinInstance = (MixinInstance) mixinIterator.next();

            for (Iterator aspectIterator = aspects.iterator(); aspectIterator.hasNext();) {
                Aspect aspect = (Aspect) aspectIterator.next();
                aspect.adviseMixin(aspectInstance, mixinInstance);
            }
        }
    }

    private void introduceMixins(AspectInstance aspectInstance) {
        for (Iterator iterator = aspects.iterator(); iterator.hasNext();) {
            Aspect aspect = (Aspect) iterator.next();
            aspect.introduce(aspectInstance);
        }
    }

    public Object newInstance(Class classIdentifier, Object[] targets) {
        AspectInstance aspectInstance = createAspectInstance(classIdentifier);
        Object proxy = aspectInstance.getProxy(false);
        setTargets(proxy, targets);
        return proxy;
    }

    public void setTargets(Object object, Object[] targets) {

        List targetsList = new ArrayList(Arrays.asList(targets));
        Collection mixins = Aspects.getAspectInstance(object).getMixins();
        for (Iterator iterator = mixins.iterator(); iterator.hasNext();) {
            final MixinInstance mixin = (MixinInstance) iterator.next();
            Object myTarget = CollectionUtils.find(targetsList, new Predicate() {
                public boolean evaluate(Object o) {
                    return mixin.getInterfaceClass().isInstance(o);
                }
            });
            mixin.setTarget(myTarget);
            targetsList.remove(myTarget);
        }
        if (!targetsList.isEmpty()) {
            throw new IllegalArgumentException("could not find mixin for target(s) " + targetsList);
        }
    }
}
