/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

/**
 * if your interceptor needs access to its definition then make sure you extend this class or one of its
 * subclass like BasicInterceptor
 *
 * TODO document DefinitionAwareInterceptor
 *
 * <!-- $Id: DefinitionAwareInterceptor.java,v 1.1 2002-11-30 18:23:56 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */

public interface DefinitionAwareInterceptor extends Interceptor {
    void setInterceptorDefinition(InterceptorDefinition interceptorDefinition);
}
