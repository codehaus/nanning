/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.samples;

import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.MethodInterceptor;
import com.tirsen.nanning.SingletonInterceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO document TraceInterceptor
 *
 * <!-- $Id: TraceInterceptor.java,v 1.4 2002-12-03 17:14:56 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.4 $
 */
public class TraceInterceptor implements MethodInterceptor, SingletonInterceptor
{
    public Object invoke(Invocation invocation) throws Throwable
    {
        StopWatch watch = new StopWatch(false);

        Log log = LogFactory.getLog(invocation.getTarget().getClass());
        StringBuffer methodCallMessage = new StringBuffer();
        methodCallMessage.append(invocation.getMethod().getName());
        methodCallMessage.append('(');
        Object[] args = invocation.getArgs();
        if (args != null) {
            for (int i = 0; i < args.length; i++)
            {
                Object arg = args[i];
                methodCallMessage.append(arg);
                if (i + 1 < args.length)
                {
                    methodCallMessage.append(", ");
                }
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
            watch.stop();
            log.error("<<< " + methodCallMessage + " threw exception, took " + watch.getTimeSpent() + " ms", e);
            throw e;
        }
        finally
        {
            watch.stop();
            log.debug("<<< " + methodCallMessage + ", took " + watch.getTimeSpent() + " ms, result " + result);
        }
    }
}
