package org.codehaus.nanning.prevayler;

import java.lang.reflect.Method;

import org.codehaus.nanning.AspectInstance;
import org.codehaus.nanning.MixinInstance;
import org.codehaus.nanning.prevayler.CheckTransactionUnsupportedInterceptor;
import org.codehaus.nanning.attribute.Attributes;
import org.codehaus.nanning.config.Aspect;
import org.codehaus.nanning.config.AttributePointcut;

/**
 * TODO document PrevaylerInterceptor
 *
 * @author <a href="mailto:jon_tirsen@yahoo.org">Jon Tirsen</a>
 * @version $Revision: 1.1 $
 */
public class PrevaylerAspect implements Aspect {
    private TransactionUnsupportedInterceptor unsupportedInterceptor;
    private CheckTransactionUnsupportedInterceptor checkUnsupportedInterceptor;
    private PrevaylerInterceptor prevaylerInterceptor;
    private RegisterObjectInterceptor registerObjectInterceptor;
    
    private AttributePointcut transactionUnsupportedPointcut = new AttributePointcut("transaction-unsupported");
    private AttributePointcut transactionPointcut = new AttributePointcut("transaction");

    public PrevaylerAspect() {
        this(false);
    }

    public PrevaylerAspect(boolean useIdentification) {
        unsupportedInterceptor = new TransactionUnsupportedInterceptor();
        checkUnsupportedInterceptor = new CheckTransactionUnsupportedInterceptor();
        prevaylerInterceptor = new PrevaylerInterceptor(useIdentification);
        
        if (useIdentification) {
            registerObjectInterceptor = new RegisterObjectInterceptor();
        }
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
    }
}
