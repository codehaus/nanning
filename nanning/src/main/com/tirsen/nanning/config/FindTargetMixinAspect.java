package com.tirsen.nanning.config;

import com.tirsen.nanning.AspectException;
import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MixinInstance;

public class FindTargetMixinAspect implements Aspect {
    private static final String DEFAULT_IMPLEMENTATION_SUFFIX = "Impl";
    private String implementationSuffix = DEFAULT_IMPLEMENTATION_SUFFIX;

    public FindTargetMixinAspect() {
        this(DEFAULT_IMPLEMENTATION_SUFFIX);
    }

    public FindTargetMixinAspect(String implementationSuffix) {
        this.implementationSuffix = implementationSuffix;
    }

    public void introduce(AspectInstance aspectInstance) {
        MixinInstance mixin = new MixinInstance();

        Class interfaceClass = aspectInstance.getClassIdentifier();
        Class targetClass = findImpl(interfaceClass);
        Object target = null;
        try {
            target = targetClass.newInstance();
        } catch (Exception e) {
            throw new AspectException("Could not instantiate target " + targetClass, e);
        }

        mixin.setInterfaceClass(interfaceClass);
        mixin.setTarget(target);
        aspectInstance.addMixin(mixin);
    }

    public void adviseMixin(AspectInstance aspectInstance, MixinInstance mixin) {
    }

    public void advise(AspectInstance aspectInstance) {
    }

    private Class findImpl(Class interfaceClass) {
        Class impl = findImpl(interfaceClass, implementationSuffix);
        assert impl != null : "could not find target for " + interfaceClass;
        return impl;
    }
    
    public static Class findImpl(Class interfaceClass, String implementationSuffix) {
        String name = interfaceClass.getName();
        int packageEnd = name.lastIndexOf('.');
        String className = name.substring(0, packageEnd) + name.substring(packageEnd) + implementationSuffix;
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
