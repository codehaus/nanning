package com.tirsen.nanning.samples.prevayler;

import java.util.Arrays;
import java.util.List;

import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MixinInstance;
import com.tirsen.nanning.config.Aspect;

/**
 * TODO document PrevaylerInterceptor
 *
 * @author <a href="mailto:jon_tirsen@yahoo.com">Jon Tirsén</a>
 * @version $Revision: 1.8 $
 */
public class PrevaylerAspect implements Aspect {
    private List interceptors;

    public PrevaylerAspect() {
        TransactionUnsupportedInterceptor unsupportedInterceptor = new TransactionUnsupportedInterceptor();
        CheckTransactionUnsupportedInterceptor checkUnsupportedInterceptor = new CheckTransactionUnsupportedInterceptor();
        PrevaylerInterceptor prevaylerInterceptor = new PrevaylerInterceptor();
        interceptors = Arrays.asList(new Object[] { unsupportedInterceptor, checkUnsupportedInterceptor, prevaylerInterceptor });
    }

    public Object advise(AspectInstance aspectInstance, MixinInstance mixin) {
        return interceptors;
    }

    public Object adviseConstruction(AspectInstance aspectInstance) {
        return null;
    }

    public Object introduce(AspectInstance aspectInstance) {
        return null;
    }
}
