package com.tirsen.nanning.prevayler;

import java.lang.reflect.Method;

import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MixinInstance;
import com.tirsen.nanning.prevayler.CheckTransactionUnsupportedInterceptor;
import com.tirsen.nanning.attribute.Attributes;
import com.tirsen.nanning.config.Aspect;
import com.tirsen.nanning.config.AttributePointcut;

/**
 * TODO document PrevaylerInterceptor
 *
 * @author <a href="mailto:jon_tirsen@yahoo.com">Jon Tirsen</a>
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
