/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.definition;

import com.tirsen.nanning.MethodInterceptor;

import java.lang.reflect.Method;

/**
 * Implement this if you want to do method-level filtering.
 *
 * <!-- $Id: FilterMethodsInterceptor.java,v 1.1 2003-01-12 13:25:40 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public interface FilterMethodsInterceptor extends MethodInterceptor
{
    boolean interceptsMethod(Method method);
}
