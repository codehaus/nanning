/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.samples;

import com.tirsen.nanning.Interceptor;
import com.tirsen.nanning.Invocation;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * TODO document TraceInterceptor
 *
 * <!-- $Id: TraceInterceptor.java,v 1.1 2002-11-18 20:56:30 tirsen Exp $ -->
 *
 * @author $Author: tirsen $
 * @version $Revision: 1.1 $
 */
public class TraceInterceptor implements Interceptor
{
    public Object invoke(Invocation invocation) throws Throwable
    {
        Log log = LogFactory.getLog(invocation.getTarget().getClass());
        StringBuffer methodCallMessage = new StringBuffer();
        methodCallMessage.append(invocation.getMethod().getName());
        methodCallMessage.append('(');
        Object[] args = invocation.getArgs();
        for (int i = 0; i < args.length; i++)
        {
            Object arg = args[i];
            methodCallMessage.append(arg);
            if (i + 1 < args.length)
            {
                methodCallMessage.append(", ");
            }
        }
        methodCallMessage.append(')');
        log.trace(">>> " + methodCallMessage);
        Object result = null;
        try
        {
            result = invocation.invokeNext();
            return result;
        }
        catch (Throwable e)
        {
            log.error("<<< " + methodCallMessage + " threw exception", e);
            throw e;
        }
        finally
        {
            log.debug("<<< " + methodCallMessage + " = " + result);
        }
    }
}
