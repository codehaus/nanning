/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import com.tirsen.nanning.Interceptor;
import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MixinInstance;

import java.lang.reflect.Method;


public interface FilterMethodsInterceptor extends Interceptor {
    boolean interceptsMethod(MixinInstance mixin, Method method);
}
