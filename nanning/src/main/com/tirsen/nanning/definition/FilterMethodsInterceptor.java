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
 * Implement this if you want to do method-level filtering when using AspectClass. Warning! If you're using
 * AspectInstance this won't work, you have to do you method-level filtering manually.
 *
 * <!-- $Id: FilterMethodsInterceptor.java,v 1.2 2003-02-06 20:33:41 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
 */
public interface FilterMethodsInterceptor extends MethodInterceptor
{
    boolean interceptsMethod(Method method);
}
