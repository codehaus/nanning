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
 * @author <a href="mailto:jon_tirsen@yahoo.com">Jon Tirsén</a>
 * @version $Revision: 1.10 $
 */
public class PrevaylerInterceptor
        implements SingletonInterceptor, FilterMethodsInterceptor, ConstructionInterceptor {

    public boolean interceptsConstructor(Class interfaceClass) {
        return Attributes.hasAttribute(interfaceClass, "entity");
    }

    public boolean interceptsMethod(Method method) {
        return Attributes.hasAttribute(method, "transaction");
    }

    public Object construct(ConstructionInvocation invocation) {
        if (!CurrentPrevayler.isInTransaction()) {
            CurrentPrevayler.enterTransaction();
            try {
                ConstructCommand command = new ConstructCommand(invocation);
                return CurrentPrevayler.getPrevayler().executeCommand(command);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                CurrentPrevayler.exitTransaction();
            }
        }
        else {
            Object object = invocation.getProxy();
            if (!CurrentPrevayler.getSystem().hasObjectID(object)) {
                CurrentPrevayler.getSystem().registerObjectID(object);
            }
            return object;
        }
    }

    public Object invoke(Invocation invocation) throws Throwable {
        if (!CurrentPrevayler.isInTransaction()) {
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
