package com.tirsen.nanning.config;

import com.tirsen.nanning.MixinInstance;
import com.tirsen.nanning.AspectInstance;

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

    protected abstract boolean adviseMethod(Method method);
}
