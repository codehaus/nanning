/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.codehaus.nanning.definition;

import org.codehaus.nanning.Interceptor;

/**
 * This is a marker-interface to indicate that a single interceptor should be used for every
 * <code>InterceptorDefinition</code> in the system.
 *
 * <!-- $Id: SingletonInterceptor.java,v 1.1 2003-07-04 10:53:57 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.1 $
 *
 * @deprecated please use the new {@link org.codehaus.nanning.config.AspectSystem} framework instead.
 * @see org.codehaus.nanning.config.InterceptorAspect.SINGLETON for example.
 */
public interface SingletonInterceptor extends Interceptor {
}
