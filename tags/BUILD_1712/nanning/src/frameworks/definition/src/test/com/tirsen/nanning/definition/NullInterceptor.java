/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.definition;

import java.lang.reflect.Method;

import com.tirsen.nanning.definition.SingletonInterceptor;
import com.tirsen.nanning.MethodInterceptor;
import com.tirsen.nanning.Invocation;

/**
 * TODO document NullInterceptor
 *
 * <!-- $Id: NullInterceptor.java,v 1.1 2003-07-01 16:08:10 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.1 $
 */
public class NullInterceptor implements MethodInterceptor, SingletonInterceptor {
    public Object invoke(Invocation invocation) throws Throwable {
        return invocation.invokeNext();
    }
}
