package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MixinInstance;
import com.tirsen.nanning.attribute.Attributes;
import com.tirsen.nanning.config.Aspect;

/**
 * TODO document PrevaylerInterceptor
 *
 * @author <a href="mailto:jon_tirsen@yahoo.com">Jon Tirs?n</a>
 * @version $Revision: 1.11 $
 */
public class PrevaylerAspect implements Aspect {
    private TransactionUnsupportedInterceptor unsupportedInterceptor;
    private CheckTransactionUnsupportedInterceptor checkUnsupportedInterceptor;
    private PrevaylerInterceptor prevaylerInterceptor;
    private RegisterObjectInterceptor registerObjectInterceptor;

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

    public void adviseMixin(AspectInstance aspectInstance, MixinInstance mixin) {
        mixin.addInterceptor(unsupportedInterceptor);
        mixin.addInterceptor(checkUnsupportedInterceptor);
        mixin.addInterceptor(prevaylerInterceptor);
    }

    public void advise(AspectInstance aspectInstance) {
        if (registerObjectInterceptor != null && Attributes.hasInheritedAttribute(aspectInstance.getClassIdentifier(), "entity")) {
            aspectInstance.addConstructionInterceptor(registerObjectInterceptor);
        }
    }

    public void introduce(AspectInstance aspectInstance) {
    }
}
