/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.codehaus.nanning;



/**
 * TODO document NullInterceptor
 *
 * <!-- $Id: NullInterceptor.java,v 1.1 2003-07-04 10:54:00 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.1 $
 */
public class NullInterceptor implements MethodInterceptor {
    public Object invoke(Invocation invocation) throws Throwable {
        return invocation.invokeNext();
    }
}
