package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.MethodInterceptor;
import com.tirsen.nanning.Invocation;

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
