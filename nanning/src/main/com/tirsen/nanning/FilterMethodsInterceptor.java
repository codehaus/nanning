/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.lang.reflect.Method;

/**
 * TODO doesn't work yet.
 *
 * <!-- $Id: FilterMethodsInterceptor.java,v 1.1 2002-11-17 14:03:34 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public interface FilterMethodsInterceptor extends Interceptor
{
    boolean interceptsMethod(Method method);
}
