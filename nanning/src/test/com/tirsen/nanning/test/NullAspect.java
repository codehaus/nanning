/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.test;

import com.tirsen.nanning.Interceptor;
import com.tirsen.nanning.Invocation;

/**
 * TODO document NullAspect
 *
 * <!-- $Id: NullAspect.java,v 1.2 2002-10-22 18:56:25 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
 */
public class NullAspect implements Interceptor
{
    public Object invoke(Invocation invocation) throws Throwable
    {
        return invocation.invokeNext();
    }
}
