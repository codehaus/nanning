package com.tirsen.nanning.samples.prevayler;

import java.lang.reflect.Method;

import com.tirsen.nanning.*;
import org.prevayler.Prevayler;

/**
 * TODO document PrevaylerInterceptor
 *
 * @author <a href="mailto:jon_tirsen@yahoo.com">Jon Tirsén</a>
 * @version $Revision: 1.6 $
 */
public class PrevaylerInterceptor
        implements SingletonInterceptor, FilterMethodsInterceptor, ConstructionInterceptor {
    private ThreadLocal inTransaction = new ThreadLocal();
    private IdentifyingSystem system;
    private Prevayler prevayler;

    public boolean interceptsConstructor(Class interfaceClass) {
        return Attributes.hasAttribute(interfaceClass, "instantiation-is-prevayler-command");
    }

    public boolean interceptsMethod(Method method) {
        return Attributes.hasAttribute(method, "prevayler-command");
    }

    public Object construct(ConstructionInvocation invocation) {
        if (!isInTransaction()) {
            enterTransaction();
            try {
                ConstructCommand command = new ConstructCommand(invocation);
                return CurrentPrevayler.getPrevayler().executeCommand(command);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                exitTransaction();
            }
        }
        else {
            Object object = invocation.getProxy();
            CurrentPrevayler.getSystem().registerOID(object);
            return object;
        }
    }

    void exitTransaction() {
        inTransaction.set(null);
    }

    void enterTransaction() {
        inTransaction.set(inTransaction); // any non-null object will do really
    }

    public Object invoke(Invocation invocation) throws Throwable {
        if (!isInTransaction()) {
            enterTransaction();
            try {
                InvokeCommand command = new InvokeCommand(invocation);
                return CurrentPrevayler.getPrevayler().executeCommand(command);
            } finally {
                exitTransaction();
            }
        } else {
            return invocation.invokeNext();
        }
    }

    private boolean isInTransaction() {
        return inTransaction.get() != null;
    }

    public void setSystem(IdentifyingSystem system) {
        this.system = system;
    }

    public IdentifyingSystem getSystem() {
        return system;
    }

    public Prevayler getPrevayler() {
        return prevayler;
    }

    public void setPrevayler(Prevayler prevayler) {
        this.prevayler = prevayler;
        setSystem((IdentifyingSystem) prevayler.system());
    }
}
