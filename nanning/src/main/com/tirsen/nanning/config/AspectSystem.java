package com.tirsen.nanning.config;

import com.tirsen.nanning.*;
import com.tirsen.nanning.definition.SingletonInterceptor;
import com.tirsen.nanning.definition.FilterMethodsInterceptor;

import java.util.*;
import java.lang.reflect.Method;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

public class AspectSystem implements AspectFactory {
    private List aspects = new ArrayList();

    public void addAspect(Aspect aspect) {
        aspects.add(aspect);
    }

    public void addPointcut(Pointcut pointcut) {
        aspects.add(new PointcutAspect(pointcut));
    }

    public Object newInstance(Object classIdentifier) {
        AspectInstance aspectInstance = new AspectInstance(this, classIdentifier);
        for (Iterator iterator = aspects.iterator(); iterator.hasNext();) {
            Aspect aspect = (Aspect) iterator.next();
            aspect.process(aspectInstance);
        }
        return aspectInstance.getProxy();
    }

    public Object newInstance(Object classIdentifier, Object[] targets) {
        Object object = newInstance(classIdentifier);
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
        return object;
    }

    public static Aspect mixin(Class interfaceClass, Class targetClass) {
        return new MixinAspect(interfaceClass, targetClass);
    }

    public static Aspect interceptor(Class interceptorClass) {
        try {
            MethodInterceptor interceptor = (MethodInterceptor) interceptorClass.newInstance();
            return interceptor(interceptor, interceptorClass);
        } catch (Exception e) {
            throw new AspectException("Could not instantiate interceptor " + e);
        }
    }

    private static Aspect interceptor(final MethodInterceptor interceptor, Class interceptorClass) {
        Advise advise;
        if (interceptor instanceof SingletonInterceptor) {
            advise = new InterceptorAdvise(interceptor);
        } else {
            assert interceptorClass != null;
            advise = new InterceptorAdvise(interceptorClass, InterceptorAdvise.PER_METHOD);
        }
        if (interceptor instanceof FilterMethodsInterceptor) {
            return new PointcutAspect(new MethodPointcut(advise) {
                public boolean adviseMethod(MixinInstance mixinInstance, Method method) {
                    return ((FilterMethodsInterceptor) interceptor).interceptsMethod(method);
                }
            });
        } else {
            return new PointcutAspect(new MethodPointcut(advise));
        }
    }

    public static Aspect interceptor(MethodInterceptor methodInterceptor) {
        assert methodInterceptor instanceof SingletonInterceptor : "must be singleton here";
        return interceptor(methodInterceptor, null);
    }

    public static Aspect constructionInterceptor(Class interceptorClass) {
        return new ConstructionInterceptorAspect(interceptorClass);
    }
}
