package com.tirsen.nanning.samples.prevayler;

import java.lang.reflect.Method;

import com.tirsen.nanning.attribute.Attributes;
import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MixinInstance;
import com.tirsen.nanning.config.ConstructionInterceptorAdvise;
import com.tirsen.nanning.config.Pointcut;
import com.tirsen.nanning.config.PointcutAspect;
import com.tirsen.nanning.config.InterceptorAdvise;

/**
 * TODO document PrevaylerInterceptor
 *
 * @author <a href="mailto:jon_tirsen@yahoo.com">Jon Tirs�n</a>
 * @version $Revision: 1.5 $
 */
public class PrevaylerAspect extends PointcutAspect {

    public PrevaylerAspect() {
        PrevaylerInterceptor prevaylerInterceptor = new PrevaylerInterceptor();
        addPointcut(new Pointcut(new ConstructionInterceptorAdvise(prevaylerInterceptor)) {
            protected boolean adviseInstance(AspectInstance aspectInstance) {
                return Attributes.hasInheritedAttribute((Class) aspectInstance.getClassIdentifier(), "entity");
            }
        });
        TransactionUnsupportedInterceptor unsupportedInterceptor = new TransactionUnsupportedInterceptor();
        addPointcut(new Pointcut(new InterceptorAdvise(unsupportedInterceptor)) {
            protected boolean adviseMethod(MixinInstance mixinInstance, Method method) {
                return Attributes.hasAttribute(method, "transaction-unsupported");
            }
        });
        CheckTransactionUnsupportedInterceptor checkUnsupportedInterceptor = new CheckTransactionUnsupportedInterceptor();
        addPointcut(new Pointcut(new InterceptorAdvise(checkUnsupportedInterceptor)) {
            protected boolean adviseMethod(MixinInstance mixinInstance, Method method) {
                return Attributes.hasAttribute(method, "transaction");
            }
        });
        addPointcut(new Pointcut(new InterceptorAdvise(prevaylerInterceptor)) {
            protected boolean adviseMethod(MixinInstance mixinInstance, Method method) {
                return Attributes.hasAttribute(method, "transaction");
            }
        });
    }

}