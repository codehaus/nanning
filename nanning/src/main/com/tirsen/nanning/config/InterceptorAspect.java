package com.tirsen.nanning.config;

import com.tirsen.nanning.AspectException;
import com.tirsen.nanning.Interceptor;
import com.tirsen.nanning.MixinInstance;
import com.tirsen.nanning.MethodInterceptor;
import com.tirsen.nanning.definition.FilterMethodsInterceptor;
import com.tirsen.nanning.definition.SingletonInterceptor;

import java.lang.reflect.Method;

public class InterceptorAspect extends PointcutAspect {
    public InterceptorAspect(Class interceptorClass) {
    }
}
