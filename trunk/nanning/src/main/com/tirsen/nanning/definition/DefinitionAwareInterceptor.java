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
 * <!-- $Id: DefinitionAwareInterceptor.java,v 1.5 2003-05-22 20:18:32 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.5 $
 *
 * @deprecated please use the new {@link com.tirsen.nanning.config.AspectSystem} framework instead.
 */
public interface DefinitionAwareInterceptor extends MethodInterceptor {
    void setInterceptorDefinition(InterceptorDefinition interceptorDefinition);
}
