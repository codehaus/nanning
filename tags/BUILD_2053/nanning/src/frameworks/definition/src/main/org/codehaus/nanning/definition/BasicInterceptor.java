/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.codehaus.nanning.definition;

import java.lang.reflect.Method;
import org.codehaus.nanning.util.Matcher;
import org.codehaus.nanning.util.RegexpPattern;

import org.codehaus.nanning.attribute.Attributes;

/**
 * basic interceptor with methodNameFilter support.
 * TODO document BasicInterceptor
 *
 * <!-- $Id: BasicInterceptor.java,v 1.2 2003-09-05 07:56:42 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.2 $
 *
 * @deprecated please use the new {@link org.codehaus.nanning.config.AspectSystem} framework instead.
 */
public abstract class BasicInterceptor implements DefinitionAwareInterceptor, FilterMethodsInterceptor {
    public static String METHOD_NAME_FILTER_ATTRIBUTE_NAME = "methodNameFilter";

    private InterceptorDefinition interceptorDefinition = null;

    /**
     * This method reads the methodNameFilter attribute from interceptor definition
     * compiles it as a pattern and matches with the passed method's name.
     * If the pattern matches it returns true.
     * If there is not methodNameFilter attribute specified then returns true.
     */
    public boolean interceptsMethod(Method method) {
        InterceptorDefinition interceptorDefinition = getInterceptorDefinition();
        String methodNameFilterPattern = (String) interceptorDefinition.getAttribute(METHOD_NAME_FILTER_ATTRIBUTE_NAME);

        if (methodNameFilterPattern != null) {
            methodNameFilterPattern = (String) interceptorDefinition.getAttribute(METHOD_NAME_FILTER_ATTRIBUTE_NAME);
        } else {
            // try the runtime-attributes
            try {
                methodNameFilterPattern = Attributes.getAttribute(this.getClass(), METHOD_NAME_FILTER_ATTRIBUTE_NAME);
                // qdox returns [.*] as [. ] so replace [. ] with [.*]; qdox should fix this or we have got to come up with a better work-around
                methodNameFilterPattern = RegexpPattern.compile(". ").matcher(methodNameFilterPattern).replaceAll(".*");

            } catch (Exception e) {
                // bad stuff happend so let it intercept
                return true;
            }

        }
        if (methodNameFilterPattern != null) {
            Matcher m = RegexpPattern.compile(methodNameFilterPattern).matcher(method.getName());
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
