package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.MethodInterceptor;
import com.tirsen.nanning.Invocation;

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
}
