package com.tirsen.nanning.config;

import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.Interceptor;
import com.tirsen.nanning.MixinInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Iterator;
import java.lang.reflect.Method;

public class PointcutAspect implements Aspect {
    private List pointcuts = new ArrayList();

    public PointcutAspect() {
    }

    public PointcutAspect(Pointcut pointcut) {
        addPointcut(pointcut);
    }

    public void addPointcut(Pointcut pointcut) {
        pointcuts.add(pointcut);
    }

    public void process(AspectInstance aspectInstance) {
        try {
            for (Iterator i = pointcuts.iterator(); i.hasNext();) {
                Pointcut pointcut = (Pointcut) i.next();
                pointcut.process(aspectInstance);
            }
            Collection mixins = aspectInstance.getMixins();
            for (Iterator iterator = mixins.iterator(); iterator.hasNext();) {
                MixinInstance mixinInstance = (MixinInstance) iterator.next();
                for (Iterator pointcutIterator = pointcuts.iterator(); pointcutIterator.hasNext();) {
                    Pointcut pointcut = (Pointcut) pointcutIterator.next();
                    pointcut.process(mixinInstance);
                }
                Method[] methods = mixinInstance.getInterfaceClass().getDeclaredMethods();
                for (int i = 0; i < methods.length; i++) {
                    Method method = methods[i];
                    for (Iterator pointcutIterator = pointcuts.iterator(); pointcutIterator.hasNext();) {
                        Pointcut pointcut = (Pointcut) pointcutIterator.next();
                        pointcut.process(mixinInstance, method);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not instantiate interceptor", e);
        }
    }
}
