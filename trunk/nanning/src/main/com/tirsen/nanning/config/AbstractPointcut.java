package com.tirsen.nanning.config;

import com.tirsen.nanning.MixinInstance;
import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MethodInterceptor;
import com.tirsen.nanning.AspectException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Method;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

public abstract class AbstractPointcut implements Pointcut {
    public Method[] methodsToAdvise(AspectInstance instance, MixinInstance mixin) {
        List methods = new ArrayList(Arrays.asList(mixin.getAllMethods()));
        CollectionUtils.filter(methods, new Predicate() {
            public boolean evaluate(Object o) {
                return adviseMethod((Method) o);
            }
        });
        return (Method[]) methods.toArray(new Method[methods.size()]);
    }

    public boolean adviseInstance(AspectInstance instance) {
        return true;
    }

    public boolean adviseMixin(MixinInstance mixin) {
        return true;
    }

    public void advise(AspectInstance instance, MethodInterceptor interceptor) {
        advise(instance, interceptor, null);
    }

    public void advise(AspectInstance instance, Class interceptorClass) {
        advise(instance, null, interceptorClass);
    }

    public void advise(AspectInstance instance, MethodInterceptor interceptor, Class interceptorClass) {
        if (adviseInstance(instance)) {
            List mixins = instance.getMixins();
            for (Iterator iterator = mixins.iterator(); iterator.hasNext();) {
                MixinInstance mixin = (MixinInstance) iterator.next();
                if (adviseMixin(mixin)) {
                    Method[] methods = methodsToAdvise(instance, mixin);
                    for (int i = 0; i < methods.length; i++) {
                        Method method = methods[i];
                        if (interceptor != null) {
                            mixin.addInterceptor(method, interceptor);
                        } else if (interceptorClass != null) {
                            try {
                                mixin.addInterceptor(method, (MethodInterceptor) interceptorClass.newInstance());
                            } catch (Exception e) {
                                throw new AspectException(e);
                            }
                        } else {
                            assert false : "interceptor or class needs to be specified";
                        }
                    }
                }
            }
        }
    }

    public abstract boolean adviseMethod(Method method);
}
