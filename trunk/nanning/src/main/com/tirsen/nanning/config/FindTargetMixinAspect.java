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

    public Object introduce(AspectInstance aspectInstance) {
        MixinInstance mixinInstance = new MixinInstance();

        Class interfaceClass = aspectInstance.getClassIdentifier();
        Class targetClass = findImpl(interfaceClass);
        Object target = null;
        try {
            target = targetClass.newInstance();
        } catch (Exception e) {
            throw new AspectException("Could not instantiate target " + targetClass, e);
        }

        mixinInstance.setInterfaceClass(interfaceClass);
        mixinInstance.setTarget(target);
        return mixinInstance;
    }

    public Object advise(AspectInstance aspectInstance, MixinInstance mixin) {
        return null;
    }

    public Object adviseConstruction(AspectInstance aspectInstance) {
        return null;
    }

    private Class findImpl(Class interfaceClass) {
        String name = interfaceClass.getName();
        int packageEnd = name.lastIndexOf('.');
        String className = name.substring(0, packageEnd) + name.substring(packageEnd) + implementationSuffix;
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            assert false : "could not find target for " + interfaceClass;
            return null;
        }
    }
}
