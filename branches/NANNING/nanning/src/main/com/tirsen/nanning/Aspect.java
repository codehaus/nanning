/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.lang.reflect.Method;

/**
 * TODO document Aspect
 *
 * <!-- $Id: Aspect.java,v 1.1.1.1 2002-10-20 09:33:53 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1.1.1 $
 */
public interface Aspect
{
    Object invoke(Method method, Object[] args, AspectContext chain) throws Throwable;
}
