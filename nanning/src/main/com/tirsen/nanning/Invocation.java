/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning;

import java.lang.reflect.Method;


/**
 * TODO document Invocation
 *
 * <!-- $Id: Invocation.java,v 1.1 2002-10-21 21:07:31 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public interface Invocation
{
    Object invokeNext(Invocation invocation) throws Throwable;

    Object getTarget();

    Object getProxy();

    int getCurrentIndex();

    int getNumberOfAspects();

    Interceptor getAspect(int index);

    Method getMethod();

    Object[] getArgs();
}
