/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.codehaus.nanning.definition;

import java.lang.reflect.Method;

import org.codehaus.nanning.definition.SingletonInterceptor;
import org.codehaus.nanning.MethodInterceptor;
import org.codehaus.nanning.Invocation;

/**
 * TODO document NullInterceptor
 *
 * <!-- $Id: NullInterceptor.java,v 1.1 2003-07-04 10:53:57 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.1 $
 */
public class NullInterceptor implements MethodInterceptor, SingletonInterceptor {
    public Object invoke(Invocation invocation) throws Throwable {
        return invocation.invokeNext();
    }
}
