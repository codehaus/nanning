package com.tirsen.nanning.config;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import com.tirsen.nanning.AspectException;
import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MixinInstance;

public class PointcutAspect implements Aspect {
    private List pointcuts = new ArrayList();
    private static Map methodsCache = new HashMap();

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

    static Method[] getAllMethods(Class klass) {
        Collection methodsCollection = getAllMethodsCollection(klass);
        Method[] results = (Method[]) methodsCache.get(klass);
        if (results == null) {
            results = (Method[]) methodsCollection.toArray(new Method[methodsCollection.size()]);
            methodsCache.put(klass, results);
        }
        return results;
    }

    private static Collection getAllMethodsCollection(Class klass) {
        Collection result = new HashSet();
        addAllMethods(klass, result);
        return result;
    }

    private static void addAllMethods(Class klass, Collection result) {
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
