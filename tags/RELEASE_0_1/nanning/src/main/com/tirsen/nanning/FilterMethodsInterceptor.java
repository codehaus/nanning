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
 * <!-- $Id: FilterMethodsInterceptor.java,v 1.3 2002-12-03 17:04:53 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.3 $
 */
public interface FilterMethodsInterceptor extends MethodInterceptor
{
    boolean interceptsMethod(Method method);
}
