/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.lang.reflect.Method;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * basic interceptor with methodNameFilter support. it would have been nicer to have
 * FilterMethodsInterceptor as Interface so that we can implement it and many others instead of directly
 * extending it.
 * TODO document BasicInterceptor
 *
 * <!-- $Id: BasicInterceptor.java,v 1.1 2002-11-30 18:23:56 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */

public abstract class BasicInterceptor implements DefinitionAwareInterceptor, FilterMethodsInterceptor {
    public static String METHOD_NAME_FILTER_ATTRIBUTE_NAME = "methodNameFilter";

    private InterceptorDefinition interceptorDefinition = null;

    /**
     * This method reads the methodNameFilter attribute from interceptor definition
     * compiles it as a pattern and matches with the passed method's name.
     * If the pattern matches it returns true.
     * If there is not methodNameFilter attribute specified then returns true.
     *
     * @param method
     * @return
     */
    public boolean interceptsMethod(Method method) {
        InterceptorDefinition interceptorDefinition = getInterceptorDefinition();
        Object objValue = interceptorDefinition.getAttribute(METHOD_NAME_FILTER_ATTRIBUTE_NAME);
        if (objValue != null) {
            String methodNameFilterPattern = (String) interceptorDefinition.getAttribute(METHOD_NAME_FILTER_ATTRIBUTE_NAME);
            Matcher m = Pattern.compile(methodNameFilterPattern).matcher(method.getName());
            return m.matches();
        }
        // no attribute specified return true.
        return true;
    }

    public InterceptorDefinition getInterceptorDefinition() {
        return interceptorDefinition;
    }

    public void setInterceptorDefinition(InterceptorDefinition interceptorDefinition) {
        this.interceptorDefinition = interceptorDefinition;
    }
}
