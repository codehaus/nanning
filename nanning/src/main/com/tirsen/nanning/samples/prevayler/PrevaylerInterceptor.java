package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.ConstructionInterceptor;
import com.tirsen.nanning.ConstructionInvocation;
import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.attribute.Attributes;
import com.tirsen.nanning.definition.FilterMethodsInterceptor;
import com.tirsen.nanning.definition.SingletonInterceptor;

import java.lang.reflect.Method;

/**
 * TODO document PrevaylerInterceptor
 *
 * @author <a href="mailto:jon_tirsen@yahoo.com">Jon Tirs�n</a>
 * @version $Revision: 1.13 $
 */
public class PrevaylerInterceptor
        implements SingletonInterceptor, FilterMethodsInterceptor, ConstructionInterceptor {

    public boolean interceptsConstructor(Class interfaceClass) {
        return Attributes.hasAttribute(interfaceClass, "entity");
    }

    public boolean interceptsMethod(Method method) {
        return Attributes.hasAttribute(method, "transaction") || Attributes.hasAttribute(method, "requires-transaction");
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
                (Identity.isService(invocation.getTargetInterface()) ||
                CurrentPrevayler.getSystem().hasObjectID(invocation.getProxy()))) {
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