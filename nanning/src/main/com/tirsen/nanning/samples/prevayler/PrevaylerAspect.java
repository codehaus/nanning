package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.*;
import com.tirsen.nanning.attribute.Attributes;
import com.tirsen.nanning.config.ConstructionInterceptorAdvise;
import com.tirsen.nanning.config.InterceptorAdvise;
import com.tirsen.nanning.config.Pointcut;
import com.tirsen.nanning.config.PointcutAspect;

import java.lang.reflect.Method;

/**
 * TODO document PrevaylerInterceptor
 *
 * @author <a href="mailto:jon_tirsen@yahoo.com">Jon Tirs�n</a>
 * @version $Revision: 1.1 $
 */
public class PrevaylerAspect extends PointcutAspect implements MethodInterceptor, ConstructionInterceptor {

    public PrevaylerAspect() {
        addPointcut(new Pointcut(new ConstructionInterceptorAdvise(this)) {
            protected boolean adviseInstance(AspectInstance aspectInstance) {
                return Attributes.hasInheritedAttribute((Class) aspectInstance.getClassIdentifier(), "entity");
            }
        });
        addPointcut(new Pointcut(new InterceptorAdvise(this)) {
            protected boolean adviseMethod(MixinInstance mixinInstance, Method method) {
                return Attributes.hasAttribute(method, "transaction");
            }
        });
    }

    public boolean interceptsConstructor(Class interfaceClass) {
        // TODO this method is gonna be removed from the interface, Real Soon Now(tm)
        return true;
    }

    public Object construct(ConstructionInvocation invocation) {
        Object object = invocation.getProxy();
        if (object instanceof IdentifyingSystem) {
            ((IdentifyingSystem) object).registerObjectID(object);
        }
        // only give object ID's if they are created inside Prevayler
        if (CurrentPrevayler.isInTransaction()) {
            if (!CurrentPrevayler.getSystem().hasObjectID(object)) {
                CurrentPrevayler.getSystem().registerObjectID(object);
            }
        }
        return object;
    }

    public Object invoke(Invocation invocation) throws Throwable {
        // only first call on objects already in Prevayler should result in a command in the log
        if (CurrentPrevayler.hasPrevayler() &&
                !CurrentPrevayler.isInTransaction() &&
                CurrentPrevayler.getSystem().hasObjectID(invocation.getProxy())) {
            CurrentPrevayler.enterTransaction();
            try {
                InvokeCommand command = new InvokeCommand(invocation);
                return CurrentPrevayler.getPrevayler().executeCommand(command);
            } finally {
                CurrentPrevayler.exitTransaction();
            }
        } else {
            return invocation.invokeNext();
        }
    }
}
