/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import com.tirsen.nanning.Interceptor;
import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MixinInstance;

import java.lang.reflect.Method;


/**
 * Implement this if you want to do method-level filtering when using AspectClass. Warning! If you're using
 * AspectInstance this won't work, you have to do you method-level filtering manually.
 *
 * <!-- $Id: FilterMethodsInterceptor.java,v 1.6 2003-05-11 13:40:51 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.6 $
 */
public interface FilterMethodsInterceptor extends MethodInterceptor {
    boolean interceptsMethod(Method method);
}
