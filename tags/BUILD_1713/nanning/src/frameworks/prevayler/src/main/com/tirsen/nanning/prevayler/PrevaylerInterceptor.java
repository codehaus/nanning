package com.tirsen.nanning.prevayler;

import java.lang.reflect.Method;

import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.MethodInterceptor;
import com.tirsen.nanning.prevayler.CurrentPrevayler;
import com.tirsen.nanning.prevayler.InvokeCommand;
import com.tirsen.nanning.attribute.Attributes;

/**
 * TODO document PrevaylerInterceptor
 *
 * @author <a href="mailto:jon_tirsen@yahoo.com">Jon Tirs?n</a>
 * @version $Revision: 1.1 $
 */
public class PrevaylerInterceptor implements MethodInterceptor {
    private boolean resolveEntities;

    public PrevaylerInterceptor(boolean resolveEntities) {
        this.resolveEntities = resolveEntities;
    }

    public boolean interceptsConstructor(Class interfaceClass) {
        return Attributes.hasAttribute(interfaceClass, "entity");
    }

    public boolean interceptsMethod(Method method) {
        return Attributes.hasAttribute(method, "transaction");
    }

    public Object invoke(Invocation invocation) throws Throwable {
        /*
        only first call on objects already in Prevayler should result in a command in the log
        ie. the following conditions should be met:
        1. there should be an active Prevayler
        2. there should NOT be a current transaction
        3. the target of the call has an object id in the current prevayler OR if the target is a service
        */

        if (CurrentPrevayler.hasPrevayler()
                && !CurrentPrevayler.isInTransaction()
                && (PrevaylerUtils.isService(invocation.getTargetInterface()) ||
                PrevaylerUtils.isEntity(invocation.getTargetInterface())))
        {
            CurrentPrevayler.enterTransaction();
            try {
                InvokeCommand command = new InvokeCommand(invocation, resolveEntities);
                return command.executeUsing(CurrentPrevayler.getPrevayler());
            } finally {
                CurrentPrevayler.exitTransaction();
            }
        } else {
            return invocation.invokeNext();
        }
    }

    static boolean transactionalReturnValue(Method method) {
        return isTransactional(method.getReturnType());
    }

    public static boolean isTransactional(Class aClass) {
        if (aClass == null) {
            return false;
        }

        Method[] methods = aClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (Attributes.hasAttribute(method, "transaction")) {
                return true;
            }
        }
        Class[] interfaces = aClass.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            Class anInterface = interfaces[i];
            if (isTransactional(anInterface)) {
                return true;
            }
        }
        if (isTransactional(aClass.getSuperclass())) {
            return true;
        }
        return false;
    }
}