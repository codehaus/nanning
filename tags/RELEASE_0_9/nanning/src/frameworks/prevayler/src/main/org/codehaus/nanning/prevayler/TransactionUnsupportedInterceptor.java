package org.codehaus.nanning.prevayler;

import java.lang.reflect.Method;

import org.codehaus.nanning.*;
import org.codehaus.nanning.prevayler.CheckTransactionUnsupportedInterceptor;
import org.codehaus.nanning.attribute.Attributes;

public class TransactionUnsupportedInterceptor implements MethodInterceptor {
    
    public Object invoke(Invocation invocation) throws Throwable {
        CheckTransactionUnsupportedInterceptor.enterTransactionsUnsupported();
        try {
            return invocation.invokeNext();
        } finally {
            CheckTransactionUnsupportedInterceptor.exitTransactionsUnsupported();
        }
    }
}
