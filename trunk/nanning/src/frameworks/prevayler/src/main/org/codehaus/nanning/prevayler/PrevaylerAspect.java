package org.codehaus.nanning.prevayler;

import org.codehaus.nanning.AspectInstance;
import org.codehaus.nanning.Mixin;
import org.codehaus.nanning.config.Aspect;
import org.codehaus.nanning.config.P;
import org.codehaus.nanning.config.Pointcut;

/**
 * TODO document PrevaylerInterceptor
 *
 * @author <a href="mailto:jon_tirsen@yahoo.org">Jon Tirsen</a>
 * @version $Revision: 1.4 $
 */
public class PrevaylerAspect implements Aspect {
    private TransactionUnsupportedInterceptor unsupportedInterceptor;
    private CheckTransactionUnsupportedInterceptor checkUnsupportedInterceptor;
    private PrevaylerInterceptor prevaylerInterceptor;
    private RegisterObjectInterceptor registerObjectInterceptor;
    
    private Pointcut transactionUnsupportedPointcut = P.methodAttribute("transaction-unsupported");
    private Pointcut transactionPointcut = P.methodAttribute("transaction");

    public PrevaylerAspect() {
        unsupportedInterceptor = new TransactionUnsupportedInterceptor();
        checkUnsupportedInterceptor = new CheckTransactionUnsupportedInterceptor();
        prevaylerInterceptor = new PrevaylerInterceptor();
        registerObjectInterceptor = new RegisterObjectInterceptor();
    }
    
    public void advise(AspectInstance aspectInstance) {
        transactionUnsupportedPointcut.advise(aspectInstance, unsupportedInterceptor);
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
