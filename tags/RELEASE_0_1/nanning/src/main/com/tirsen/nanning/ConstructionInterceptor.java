package com.tirsen.nanning;

public interface ConstructionInterceptor extends Interceptor {
    Object construct(ConstructionInvocation invocation);

    boolean interceptsConstructor(Class interfaceClass);
}
