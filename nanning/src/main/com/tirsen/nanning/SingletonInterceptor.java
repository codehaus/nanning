/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

/**
 * This is a marker-interface to indicate that a single interceptor should be used for every
 * <code>InterceptorDefinition</code> in the system.
 *
 * <!-- $Id: SingletonInterceptor.java,v 1.2 2002-12-03 17:05:08 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.2 $
 */
public interface SingletonInterceptor extends MethodInterceptor
{
}
