/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import com.tirsen.nanning.MethodInterceptor;
import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.SingletonInterceptor;

/**
 * TODO document NullInterceptor
 *
 * <!-- $Id: NullInterceptor.java,v 1.2 2002-12-03 17:21:01 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.2 $
 */
public class NullInterceptor implements MethodInterceptor, SingletonInterceptor
{
    public Object invoke(Invocation invocation) throws Throwable
    {
        return invocation.invokeNext();
    }
}
