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
 * <!-- $Id: DefinitionAwareInterceptor.java,v 1.4 2003-05-11 13:40:52 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.4 $
 */

public interface DefinitionAwareInterceptor extends MethodInterceptor {
    void setInterceptorDefinition(InterceptorDefinition interceptorDefinition);
}
