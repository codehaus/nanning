package com.tirsen.nanning;

public interface ConstructionInvocation {
    Object getProxy();

    Object getTarget();

    void setTarget(Object newTarget);
}
