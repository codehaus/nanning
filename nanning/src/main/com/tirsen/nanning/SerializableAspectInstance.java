package com.tirsen.nanning;

import java.io.Serializable;

public class SerializableAspectInstance implements Serializable {
    private Class interfaceClass;
    private Object[] targets;

    public SerializableAspectInstance(Object object) {
        AspectInstance aspectInstance = Aspects.getAspectInstance(object);
        interfaceClass = aspectInstance.getAspectClass().getInterfaceClass();
        targets = aspectInstance.getTargets();
    }

    public Object getObject() {
        return Aspects.getCurrentAspectRepository().newInstance(interfaceClass, targets);
    }
}
