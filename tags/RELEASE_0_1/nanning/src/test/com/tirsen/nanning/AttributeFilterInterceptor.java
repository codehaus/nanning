/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package com.tirsen.nanning;

import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.BasicInterceptor;

import java.util.ArrayList;
import java.util.List;

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