package com.tirsen.nanning.samples.prevayler;

import java.lang.reflect.Method;

import com.tirsen.nanning.MethodInterceptor;
import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.MixinInstance;
import com.tirsen.nanning.AspectInstance;
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

    public boolean interceptsMethod(AspectInstance aspectInstance, MixinInstance mixin, Method method) {
        return Attributes.hasAttribute(method, "transaction-unsupported");
    }
}
