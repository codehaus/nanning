/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.tirsen.nanning.samples;

import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.MethodInterceptor;
import com.tirsen.nanning.definition.SingletonInterceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO document TraceInterceptor
 *
 * <!-- $Id: TraceInterceptor.java,v 1.7 2003-03-21 17:11:12 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.7 $
 */
public class TraceInterceptor implements MethodInterceptor, SingletonInterceptor {
    public Object invoke(Invocation invocation) throws Throwable {
        StopWatch watch = new StopWatch(false);

        Log log = LogFactory.getLog(invocation.getTarget().getClass());
        StringBuffer methodCallMessage = new StringBuffer();
        methodCallMessage.append(invocation.getMethod().getName());
        methodCallMessage.append('(');
        Object[] args = invocation.getArgs();
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                methodCallMessage.append(arg);
                if (i + 1 < args.length) {
                    methodCallMessage.append(", ");
                }
            }
        }
        methodCallMessage.append(')');
        log.trace(">>> " + methodCallMessage);
        Object result = null;
        try {
            result = invocation.invokeNext();
            return result;
        } catch (Throwable e) {
            watch.stop();
            log.error("<<< " + methodCallMessage + " threw exception, took " + (int) watch.getTimeSpent() + " ms", e);
            throw e;
        } finally {
            watch.stop();
            log.debug("<<< " + methodCallMessage + ", took " + (int) watch.getTimeSpent() + " ms, result " + result);
        }
    }
}
