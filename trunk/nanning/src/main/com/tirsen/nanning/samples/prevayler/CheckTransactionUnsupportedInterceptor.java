package com.tirsen.nanning.samples.prevayler;

import java.lang.reflect.Method;

import com.tirsen.nanning.MethodInterceptor;
import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.MixinInstance;
import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.attribute.Attributes;

public class CheckTransactionUnsupportedInterceptor implements MethodInterceptor {
    private static ThreadLocal transactionsUnsupported = new ThreadLocal();

    static boolean isTransactionsSupported() {
        return transactionsUnsupported.get() == null;
    }

    static void enterTransactionsUnsupported() {
        transactionsUnsupported.set(transactionsUnsupported);
    }

    static void exitTransactionsUnsupported() {
        transactionsUnsupported.set(null);
    }

    public Object invoke(Invocation invocation) throws Throwable {
        if (!isTransactionsSupported()) {
            throw new IllegalStateException("Transactions are not supported in the current calling context.");
        }

        return invocation.invokeNext();
    }

    public boolean interceptsMethod(AspectInstance aspectInstance, MixinInstance mixin, Method method) {
        return Attributes.hasAttribute(method, "transaction");
    }
}
