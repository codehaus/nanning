package com.tirsen.nanning.samples.prevayler;

import java.lang.reflect.Method;

import com.tirsen.nanning.*;
import com.tirsen.nanning.attribute.Attributes;

public class TransactionUnsupportedInterceptor implements MethodInterceptor, FilterMethodsInterceptor  {
    
    public Object invoke(Invocation invocation) throws Throwable {
        CheckTransactionUnsupportedInterceptor.enterTransactionsUnsupported();
        try {
            return invocation.invokeNext();
        } finally {
            CheckTransactionUnsupportedInterceptor.exitTransactionsUnsupported();
        }
    }

    public boolean interceptsMethod(Method method) {
        return Attributes.hasAttribute(method, "transaction-unsupported");
    }
}