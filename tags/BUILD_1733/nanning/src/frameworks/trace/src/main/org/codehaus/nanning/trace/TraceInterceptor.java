/*
 * Nanning Aspects
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.codehaus.nanning.trace;

import org.codehaus.nanning.Invocation;
import org.codehaus.nanning.MethodInterceptor;
import org.codehaus.nanning.samples.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO document TraceInterceptor
 *
 * <!-- $Id: TraceInterceptor.java,v 1.1 2003-07-04 10:53:58 lecando Exp $ -->
 *
 * @author $Author: lecando $
 * @version $Revision: 1.1 $
 */
public class TraceInterceptor implements MethodInterceptor {
    private Log logger;

    public TraceInterceptor(Log logger) {
        this.logger = logger;
    }

    public TraceInterceptor() {
    }

    public Object invoke(Invocation invocation) throws Throwable {
        StopWatch watch = new StopWatch(false);

        Log logger = getLogger(invocation.getTarget().getClass());

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
        logger.debug(">>> " + methodCallMessage);
        Object result = null;
        try {
            result = invocation.invokeNext();
            return result;
        } catch (Throwable e) {
            watch.stop();
            logger.error("<<< " + methodCallMessage + " threw exception, took " + (int) watch.getTimeSpent() + " ms", e);
            throw e;
        } finally {
            watch.stop();
            logger.debug("<<< " + methodCallMessage + ", took " + (int) watch.getTimeSpent() + " ms, result " + result);
        }
    }

    private Log getLogger(Class targetClass) {
        Log logger;
        logger = this.logger;
        if (logger == null) {
            logger = LogFactory.getLog(targetClass);
        }
        return logger;
    }
}
