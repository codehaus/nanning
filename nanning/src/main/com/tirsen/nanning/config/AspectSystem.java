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

        introduceMixin(aspectInstance);
        adviceConstruction(aspectInstance);
        advice(aspectInstance);

        return aspectInstance;
    }

    private void adviceConstruction(AspectInstance aspectInstance) {
        for (Iterator aspectIterator = aspects.iterator(); aspectIterator.hasNext();) {
            Aspect aspect = (Aspect) aspectIterator.next();

            Object result = aspect.adviseConstruction(aspectInstance);
            if (result instanceof Collection) {
                Collection interceptors = (Collection) result;
                for (Iterator i = interceptors.iterator(); i.hasNext();) {
                    ConstructionInterceptor interceptor = (ConstructionInterceptor) i.next();
                    aspectInstance.addConstructionInterceptor(interceptor);
                }
            } else if (result != null) {
                aspectInstance.addConstructionInterceptor((ConstructionInterceptor) result);
            }
        }
    }

    private void advice(AspectInstance aspectInstance) {
        for (Iterator mixinIterator = aspectInstance.getMixins().iterator(); mixinIterator.hasNext();) {
            MixinInstance mixinInstance = (MixinInstance) mixinIterator.next();

            for (Iterator aspectIterator = aspects.iterator(); aspectIterator.hasNext();) {
                Aspect aspect = (Aspect) aspectIterator.next();

                Object result = aspect.advise(aspectInstance, mixinInstance);
                if (result instanceof Collection) {
                    Collection interceptors = (Collection) result;
                    for (Iterator i = interceptors.iterator(); i.hasNext();) {
                        mixinInstance.addInterceptor(aspectInstance, (Interceptor) i.next());
                    }
                } else if (result != null) {
                    mixinInstance.addInterceptor(aspectInstance, (Interceptor) result);
                }
            }
        }
    }

    private void introduceMixin(AspectInstance aspectInstance) {
        for (Iterator iterator = aspects.iterator(); iterator.hasNext();) {
            Aspect aspect = (Aspect) iterator.next();
            Object result = aspect.introduce(aspectInstance);
            if (result instanceof Collection) {
                Collection mixins = (Collection) result;
                for (Iterator i = mixins.iterator(); i.hasNext();) {
                    MixinInstance mixinInstance = (MixinInstance) i.next();
                    aspectInstance.addMixin(mixinInstance);
                }
            } else if (result != null) {
                aspectInstance.addMixin((MixinInstance) result);
            }
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
