/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.definition;

import com.tirsen.nanning.Interceptor;

/**
 * This is a marker-interface to indicate that a single interceptor should be used for every
 * <code>InterceptorDefinition</code> in the system.
 *
 * <!-- $Id: SingletonInterceptor.java,v 1.1 2003-07-01 16:08:10 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.1 $
 *
 * @deprecated please use the new {@link com.tirsen.nanning.config.AspectSystem} framework instead.
 * @see com.tirsen.nanning.config.InterceptorAspect.SINGLETON for example.
 */
public interface SingletonInterceptor extends Interceptor {
}
