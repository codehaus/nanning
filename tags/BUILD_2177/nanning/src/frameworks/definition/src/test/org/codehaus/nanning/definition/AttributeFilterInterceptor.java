/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package org.codehaus.nanning.definition;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.nanning.definition.BasicInterceptor;
import org.codehaus.nanning.Invocation;

/**
 * @methodNameFilter .*Again
 */
public class AttributeFilterInterceptor extends BasicInterceptor {

    public static List invokedMethods = new ArrayList();

    public Object invoke(Invocation invocation) throws Throwable {
        String methodName = invocation.getMethod().getName();
        invokedMethods.add(methodName);
        return invocation.invokeNext();
    }

}