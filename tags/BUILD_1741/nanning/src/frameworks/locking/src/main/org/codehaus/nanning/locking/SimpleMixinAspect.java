package org.codehaus.nanning.locking;

import org.codehaus.nanning.config.Aspect;
import org.codehaus.nanning.AspectInstance;
import org.codehaus.nanning.Mixin;
import org.codehaus.nanning.AspectException;

import java.util.Arrays;

/**
 * Abstract utility base-classes for aspects that themselves add state and behaviour to objects. The
 * aspect itself will be cloned and introduced as the target of a mixin. It can also add interceptors that work on
 * the state and behavior introduced.
 */
public abstract class SimpleMixinAspect implements Aspect, Cloneable {
    private Class interfaceClass;

    /**
     * Subclasses using this constructor should implement one and only one interface, this interface will
     * be used as the interface for the mixin.
     */
    public SimpleMixinAspect() {
        setInterfaceClass(determineInterfaceClass(this.getClass()));
    }

    private Class determineInterfaceClass(Class targetClass) {
        Class[] interfaces = targetClass.getInterfaces();
        while (targetClass.getInterfaces().length == 0) {
            targetClass = targetClass.getSuperclass();
            interfaces = targetClass.getInterfaces();
        }
        assert targetClass != SimpleMixinAspect.class && interfaces.length == 1 :
                "your aspects class " + targetClass + " does not implement exactly one interface  " + Arrays.asList(interfaces) +
                " you have to specify the mixins interface manually using setInterfaceClass(Class)";
        Class interfaceClass = interfaces[0];
        return interfaceClass;
    }

    public SimpleMixinAspect(Class interfaceClass) {
        setInterfaceClass(interfaceClass);
    }

    protected void setInterfaceClass(Class interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public void introduce(AspectInstance instance) {
        try {
            instance.addMixin(new Mixin(interfaceClass, clone()));
        } catch (Exception e) {
            throw new AspectException(e);
        }
    }

    public void advise(AspectInstance aspectInstance) {
        Object target = aspectInstance.getMixinForInterface(interfaceClass).getTarget();
        if (target == this) {
            doAdvise(aspectInstance);

        } else {
            ((Aspect) target).advise(aspectInstance);
        }
    }

    protected abstract void doAdvise(AspectInstance aspectInstance);
}
