package com.tirsen.nanning.samples.prevayler;

import java.util.Arrays;
import java.util.List;
import java.util.Iterator;

import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MixinInstance;
import com.tirsen.nanning.config.Aspect;

/**
 * TODO document PrevaylerInterceptor
 *
 * @author <a href="mailto:jon_tirsen@yahoo.com">Jon Tirs?n</a>
 * @version $Revision: 1.9 $
 */
public class PrevaylerAspect implements Aspect {
    private TransactionUnsupportedInterceptor unsupportedInterceptor;
    private CheckTransactionUnsupportedInterceptor checkUnsupportedInterceptor;
    private PrevaylerInterceptor prevaylerInterceptor;

    public PrevaylerAspect() {
        unsupportedInterceptor = new TransactionUnsupportedInterceptor();
        checkUnsupportedInterceptor = new CheckTransactionUnsupportedInterceptor();
        prevaylerInterceptor = new PrevaylerInterceptor();
    }

    public void adviseMixin(AspectInstance aspectInstance, MixinInstance mixin) {
        mixin.addInterceptor(unsupportedInterceptor);
        mixin.addInterceptor(checkUnsupportedInterceptor);
        mixin.addInterceptor(prevaylerInterceptor);
    }

    public void advise(AspectInstance aspectInstance) {
    }

    public void introduce(AspectInstance aspectInstance) {
    }
}
