package com.tirsen.nanning.config;

import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.Interceptor;
import com.tirsen.nanning.MixinInstance;
import com.tirsen.nanning.AspectException;

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
                Method[] methods = getAllMethods(mixinInstance.getInterfaceClass());
                for (int i = 0; i < methods.length; i++) {
                    Method method = methods[i];
                    for (Iterator pointcutIterator = pointcuts.iterator(); pointcutIterator.hasNext();) {
                        Pointcut pointcut = (Pointcut) pointcutIterator.next();
                        pointcut.process(mixinInstance, method);
                    }
                }
            }
        } catch (Exception e) {
            throw new AspectException("Could not process aspect " + this, e);
        }
    }

    private Method[] getAllMethods(Class klass) {
        List result = getAllMethodsList(klass);
        return (Method[]) result.toArray(new Method[result.size()]);
    }

    private List getAllMethodsList(Class klass) {
        ArrayList result = new ArrayList();
        addAllMethods(klass, result);
        return result;
    }

    private void addAllMethods(Class klass, List result) {
        if (klass != null) {
            Method[] methods = klass.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                result.add(method);
            }
            Class[] interfaces = klass.getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                Class intf = interfaces[i];
                addAllMethods(intf, result);
            }
            addAllMethods(klass.getSuperclass(), result);
        }
    }
}
