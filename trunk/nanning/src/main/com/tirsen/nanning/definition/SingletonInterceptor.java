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
 * <!-- $Id: SingletonInterceptor.java,v 1.2 2003-05-22 20:18:32 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
 *
 * @deprecated please use the new {@link com.tirsen.nanning.config.AspectSystem} framework instead.
 * @see com.tirsen.nanning.config.InterceptorAspect.SINGLETON for example.
 */
public interface SingletonInterceptor extends Interceptor {
}
