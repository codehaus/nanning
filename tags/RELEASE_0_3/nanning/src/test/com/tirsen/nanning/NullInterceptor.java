/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import com.tirsen.nanning.definition.SingletonInterceptor;

/**
 * TODO document NullInterceptor
 *
 * <!-- $Id: NullInterceptor.java,v 1.5 2003-03-21 17:11:14 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.5 $
 */
public class NullInterceptor implements MethodInterceptor, SingletonInterceptor {
    public Object invoke(Invocation invocation) throws Throwable {
        return invocation.invokeNext();
    }
}
