/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.lang.reflect.Method;

import com.tirsen.nanning.definition.SingletonInterceptor;

/**
 * TODO document NullInterceptor
 *
 * <!-- $Id: NullInterceptor.java,v 1.7 2003-05-11 11:17:17 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.7 $
 */
public class NullInterceptor implements MethodInterceptor, SingletonInterceptor {
    public Object invoke(Invocation invocation) throws Throwable {
        return invocation.invokeNext();
    }
}
