package com.tirsen.nanning.config;

import com.tirsen.nanning.AspectException;
import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MixinInstance;

public class FindTargetMixinAspect extends PointcutAspect {
    private static final String IMPLEMENTATION_SUFFIX = "Impl";

    public FindTargetMixinAspect() {
        Advise advise = new Advise() {
            public void advise(AspectInstance aspectInstance) {
                Class interfaceClass = (Class) aspectInstance.getClassIdentifier();
                Class targetClass = findImpl(interfaceClass);
                Object target = null;
                try {
                    target = targetClass.newInstance();
                } catch (Exception e) {
                    throw new AspectException("Could not instantiate target " + targetClass, e);
                }

                MixinInstance mixinInstance = new MixinInstance();
                mixinInstance.setInterfaceClass(interfaceClass);
                mixinInstance.setTarget(target);
                aspectInstance.addMixin(mixinInstance);
            }
        };
        Pointcut pointcut = new AllPointcut();
        pointcut.addAdvise(advise);
        addPointcut(pointcut);
    }

    private Class findImpl(Class interfaceClass) {
        String name = interfaceClass.getName();
        int packageEnd = name.lastIndexOf('.');
        String className = name.substring(0, packageEnd) + name.substring(packageEnd) + IMPLEMENTATION_SUFFIX;
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            assert false : "could not find target for " + interfaceClass;
            return null;
        }
    }
}