/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.definition;

import com.tirsen.nanning.MethodInterceptor;

/**
 * if your interceptor needs access to its definition then make sure you extend this class or one of its
 * subclass like BasicInterceptor
 *
 * TODO document DefinitionAwareInterceptor
 *
 * <!-- $Id: DefinitionAwareInterceptor.java,v 1.2 2003-01-24 13:29:30 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.2 $
 */

public interface DefinitionAwareInterceptor extends MethodInterceptor {
    void setInterceptorDefinition(InterceptorDefinition interceptorDefinition);
}
