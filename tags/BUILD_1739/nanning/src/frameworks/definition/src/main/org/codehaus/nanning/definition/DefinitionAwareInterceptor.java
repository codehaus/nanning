/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.codehaus.nanning.definition;

import org.codehaus.nanning.MethodInterceptor;

/**
 * if your interceptor needs access to its definition then make sure you extend this class or one of its
 * subclass like BasicInterceptor
 *
 * TODO document DefinitionAwareInterceptor
 *
 * <!-- $Id: DefinitionAwareInterceptor.java,v 1.1 2003-07-04 10:53:57 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.1 $
 *
 * @deprecated please use the new {@link org.codehaus.nanning.config.AspectSystem} framework instead.
 */
public interface DefinitionAwareInterceptor extends MethodInterceptor {
    void setInterceptorDefinition(InterceptorDefinition interceptorDefinition);
}
