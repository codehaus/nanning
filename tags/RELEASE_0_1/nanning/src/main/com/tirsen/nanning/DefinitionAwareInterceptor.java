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
 * <!-- $Id: DefinitionAwareInterceptor.java,v 1.2 2002-12-03 17:04:50 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.2 $
 */

public interface DefinitionAwareInterceptor extends MethodInterceptor {
    void setInterceptorDefinition(InterceptorDefinition interceptorDefinition);
}
