package com.tirsen.nanning.config;

import com.tirsen.nanning.AspectException;
import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MethodInterceptor;
import com.tirsen.nanning.MixinInstance;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractPointcut implements Pointcut {
    public Method[] methodsToAdvise(AspectInstance instance, MixinInstance mixin) {
        Method[] methods = mixin.getAllMethods();
        List methodsToAdvise = new ArrayList();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (adviseMethod(method)) {
                methodsToAdvise.add(method);
            }
        }
        return (Method[]) methodsToAdvise.toArray(new Method[methodsToAdvise.size()]);
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
