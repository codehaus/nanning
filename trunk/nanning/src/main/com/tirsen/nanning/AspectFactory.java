package com.tirsen.nanning;

public interface AspectFactory {
    Object newInstance(Object classIdentifier);

    Object newInstance(Object classIdentifier, Object[] targets);
}
