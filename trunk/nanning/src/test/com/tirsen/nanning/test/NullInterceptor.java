/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.test;

import com.tirsen.nanning.Interceptor;
import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.StatelessInterceptor;

/**
 * TODO document NullInterceptor
 *
 * <!-- $Id: NullInterceptor.java,v 1.2 2002-11-05 20:46:39 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
 */
public class NullInterceptor implements Interceptor, StatelessInterceptor
{
    public Object invoke(Invocation invocation) throws Throwable
    {
        return invocation.invokeNext();
    }
}
