package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.*;
import org.prevayler.Prevayler;

import java.lang.reflect.Method;

/**
 * TODO document PrevaylerInterceptor
 *
 * @author <a href="mailto:jon_tirsen@yahoo.com">Jon Tirsén</a>
 * @version $Revision: 1.2 $
 */
public class PrevaylerInterceptor
        implements SingletonInterceptor, FilterMethodsInterceptor, DefinitionAwareInterceptor
{
    private ThreadLocal inTransaction = new ThreadLocal();
    private Class commandClass;
    private Prevayler prevayler;

    public void setInterceptorDefinition(InterceptorDefinition interceptorDefinition) {
        try {
            setCommandClass(Class.forName((String) interceptorDefinition.getAttribute("commandClass")));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        setPrevayler((Prevayler) interceptorDefinition.getAttribute("prevayler"));
    }

    public void setCommandClass(Class commandClass) {
        this.commandClass = commandClass;
    }

    public void setPrevayler(Prevayler prevayler) {
        this.prevayler = prevayler;
    }

    public boolean interceptsMethod(Method method) {
        return Attributes.hasAttribute(method, "prevayler-command");
    }

    public Object invoke(Invocation invocation) throws Throwable {
        if(!isInTransaction()) {
            InvocationCommand command = (InvocationCommand) commandClass.newInstance();
            command.setInvocation(invocation);
            return prevayler.executeCommand(command);
        }
        else {
            return invocation.invokeNext();
        }
    }

    private boolean isInTransaction() {
        return inTransaction.get() != null;
    }
}
