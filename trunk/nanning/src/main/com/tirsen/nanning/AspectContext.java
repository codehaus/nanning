/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.lang.reflect.Method;

/**
 * TODO document AspectContext
 *
 * <!-- $Id: AspectContext.java,v 1.1.1.1 2002-10-20 09:33:53 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1.1.1 $
 */
public interface AspectContext
{
    Object invokeNext(Method method, Object[] args, AspectContext aspectChain) throws Throwable;

    Object getRealObject();

    Object getProxy();
}
