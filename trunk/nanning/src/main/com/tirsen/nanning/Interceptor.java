/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;



/**
 * TODO document Interceptor
 *
 * <!-- $Id: Interceptor.java,v 1.1 2002-10-21 21:07:31 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public interface Interceptor
{
    Object invoke(Invocation invocation) throws Throwable;
}
