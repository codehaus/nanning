package com.tirsen.nanning.prevayler;

import java.lang.reflect.Method;

import com.tirsen.nanning.*;
import com.tirsen.nanning.prevayler.CheckTransactionUnsupportedInterceptor;
import com.tirsen.nanning.attribute.Attributes;

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
