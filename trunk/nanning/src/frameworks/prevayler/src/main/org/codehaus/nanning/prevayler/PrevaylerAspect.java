package org.codehaus.nanning.prevayler;

import org.codehaus.nanning.AspectInstance;
import org.codehaus.nanning.Mixin;
import org.codehaus.nanning.MethodInterceptor;
import org.codehaus.nanning.Invocation;
import org.codehaus.nanning.config.Aspect;
import org.codehaus.nanning.config.P;
import org.codehaus.nanning.config.Pointcut;

/**
 * TODO document PrevaylerInterceptor
 *
 * @author <a href="mailto:jon_tirsen@yahoo.org">Jon Tirsen</a>
 * @version $Revision: 1.5 $
 */
public class PrevaylerAspect implements Aspect {
    private Pointcut transactionUnsupportedPointcut = P.methodAttribute("transaction-unsupported");
    private MethodInterceptor transactionUnsupportedInterceptor = new MethodInterceptor() {
        public Object invoke(Invocation invocation) throws Throwable {
            enterTransactionsUnsupported();
            try {
                return invocation.invokeNext();
            } finally {
                exitTransactionsUnsupported();
            }
        }
    };
    private MethodInterceptor checkUnsupportedInterceptor = new MethodInterceptor() {
        public Object invoke(Invocation invocation) throws Throwable {
            if (!isTransactionsSupported()) {
                throw new IllegalStateException("Transactions are not supported in the current calling context.");
            }
            return null;
        }
    };

    private Pointcut transactionRequiredPointcut = P.methodAttribute("transaction-required");

    private Pointcut transactionPointcut = P.methodAttribute("transaction");
    private PrevaylerInterceptor prevaylerInterceptor;
    private RegisterObjectInterceptor registerObjectInterceptor;
    private MethodInterceptor transactionRequiredInterceptor = new MethodInterceptor() {
        public Object invoke(Invocation invocation) throws Throwable {
            if (!CurrentPrevayler.isInTransaction()) {
                throw new TransactionRequiredException();
            }
            return invocation.invokeNext();
        }
    };


    public PrevaylerAspect() {
        transactionUnsupportedInterceptor = new TransactionUnsupportedInterceptor();
        checkUnsupportedInterceptor = new CheckTransactionUnsupportedInterceptor();
        prevaylerInterceptor = new PrevaylerInterceptor();
        registerObjectInterceptor = new RegisterObjectInterceptor();
    }

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

    public void advise(AspectInstance aspectInstance) {
        transactionRequiredPointcut.advise(aspectInstance, transactionRequiredInterceptor);
        transactionUnsupportedPointcut.advise(aspectInstance, transactionUnsupportedInterceptor);
        transactionPointcut.advise(aspectInstance, checkUnsupportedInterceptor);
        transactionPointcut.advise(aspectInstance, prevaylerInterceptor);

        if (registerObjectInterceptor != null && PrevaylerUtils.isEntity(aspectInstance.getClassIdentifier())) {
            aspectInstance.addConstructionInterceptor(registerObjectInterceptor);
        }
    }

    public void introduce(AspectInstance aspectInstance) {
        if (PrevaylerUtils.isEntity(aspectInstance.getClassIdentifier())) {
            aspectInstance.addMixin(new Mixin(Identifiable.class, new IdentifiableImpl()));
        }
    }
}
