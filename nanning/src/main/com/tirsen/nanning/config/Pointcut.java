package com.tirsen.nanning.config;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MixinInstance;

/**
 * Pointcuts selects methods, mixins or aspect instances and applies one or more advises to this selection.
 */
public class Pointcut {
    private List advises = new ArrayList();

    public Pointcut() {
    }

    public Pointcut(Advise advise) {
        addAdvise(advise);
    }

    public void addAdvise(Advise advise) {
        advises.add(advise);
    }

    public void process(AspectInstance aspectInstance) {
        if (adviseInstance(aspectInstance)) {
            for (Iterator iterator = advises.iterator(); iterator.hasNext();) {
                Advise advise = (Advise) iterator.next();
                advise.advise(aspectInstance);
            }
        }
    }

    public void process(MixinInstance mixinInstance) {
        if (adviseMixin(mixinInstance)) {
            for (Iterator iterator = advises.iterator(); iterator.hasNext();) {
                Advise advise = (Advise) iterator.next();
                advise.advise(mixinInstance);
            }
        }
    }

    public void process(MixinInstance mixinInstance, Method method) {
        if (adviseMethod(mixinInstance, method)) {
            for (Iterator iterator = advises.iterator(); iterator.hasNext();) {
                Advise advise = (Advise) iterator.next();
                advise.advise(mixinInstance, method);
            }
        }
    }

    /**
     * Override if advises should be applied to the whole instance, will not affect wheather advises are applied
     * to method or mixin.
     * @param aspectInstance instance to decide wheather to advise or not.
     * @return <code>true</code> will result in advises being applied to instance, <code>false</code>
     */
    protected boolean adviseInstance(AspectInstance aspectInstance) {
        return false;
    }

    protected boolean adviseMixin(MixinInstance mixinInstance) {
        return false;
    }

    protected boolean adviseMethod(MixinInstance mixinInstance, Method method) {
        return false;
    }
}
