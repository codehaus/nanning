/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.definition;

import java.lang.reflect.Method;

import com.tirsen.nanning.MethodInterceptor;

/**
 * Implement this if you want to do method-level filtering when using AspectClass. Warning! If you're using
 * AspectInstance this won't work, you have to do you method-level filtering manually.
 *
 * <!-- $Id: FilterMethodsInterceptor.java,v 1.3 2003-03-21 17:11:11 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.3 $
 */
public interface FilterMethodsInterceptor extends MethodInterceptor {
    boolean interceptsMethod(Method method);
}
