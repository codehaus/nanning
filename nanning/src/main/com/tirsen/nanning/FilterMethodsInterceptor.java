/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.lang.reflect.Method;

/**
 * Implement this if you want to do method-level filtering.
 *
 * <!-- $Id: FilterMethodsInterceptor.java,v 1.2 2002-11-18 20:56:30 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
 */
public interface FilterMethodsInterceptor extends Interceptor
{
    boolean interceptsMethod(Method method);
}
