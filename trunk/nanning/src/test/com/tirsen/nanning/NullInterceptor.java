/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;



/**
 * TODO document NullInterceptor
 *
 * <!-- $Id: NullInterceptor.java,v 1.8 2003-07-01 16:08:10 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.8 $
 */
public class NullInterceptor implements MethodInterceptor {
    public Object invoke(Invocation invocation) throws Throwable {
        return invocation.invokeNext();
    }
}
