package com.tirsen.nanning.config;

import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MixinInstance;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.lang.reflect.Method;

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
