/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import com.tirsen.nanning.Interceptor;
import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.SingletonInterceptor;

/**
 * TODO document NullInterceptor
 *
 * <!-- $Id: NullInterceptor.java,v 1.1 2002-11-17 14:03:34 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public class NullInterceptor implements Interceptor, SingletonInterceptor
{
    public Object invoke(Invocation invocation) throws Throwable
    {
        return invocation.invokeNext();
    }
}
