package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.*;
import org.prevayler.Prevayler;

import java.lang.reflect.Method;

/**
 * TODO document PrevaylerInterceptor
 *
 * @author <a href="mailto:jon_tirsen@yahoo.com">Jon Tirsén</a>
 * @version $Revision: 1.4 $
 */
public class PrevaylerInterceptor
        implements SingletonInterceptor, FilterMethodsInterceptor, DefinitionAwareInterceptor, ConstructionInterceptor {
    private ThreadLocal inTransaction = new ThreadLocal();
    private Class constructCommandClass;
    private Class invokeCommandClass;
    private Prevayler prevayler;

    public void setInterceptorDefinition(InterceptorDefinition interceptorDefinition) {
        String constructCommandClassName = (String) interceptorDefinition.getAttribute("constructCommandClass");
        if (constructCommandClassName != null) {
            try {
                setConstructCommandClass(Class.forName(constructCommandClassName));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        String invokeCommandClassName = (String) interceptorDefinition.getAttribute("invokeCommandClass");
        if (invokeCommandClassName != null) {
            try {
                setInvokeCommandClass(Class.forName(invokeCommandClassName));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        Prevayler prevayler = (Prevayler) interceptorDefinition.getAttribute("prevayler");
        if (prevayler != null) {
            setPrevayler(prevayler);
        }
    }

    public void setConstructCommandClass(Class constructCommandClass) {
        this.constructCommandClass = constructCommandClass;
    }

    public void setInvokeCommandClass(Class invokeCommandClass) {
        this.invokeCommandClass = invokeCommandClass;
    }

    public void setPrevayler(Prevayler prevayler) {
        this.prevayler = prevayler;
    }

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
                ConstructCommand command = (ConstructCommand) constructCommandClass.newInstance();
                command.setInvocation(invocation);
                return prevayler.executeCommand(command);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                exitTransaction();
            }
        }
        else {
            return invocation.getProxy();
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
                InvocationCommand command = (InvocationCommand) invokeCommandClass.newInstance();
                command.setInvocation(invocation);
                return prevayler.executeCommand(command);
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
}
