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
 * <!-- $Id: SingletonInterceptor.java,v 1.1 2003-01-12 13:25:40 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public interface SingletonInterceptor extends Interceptor {
}
