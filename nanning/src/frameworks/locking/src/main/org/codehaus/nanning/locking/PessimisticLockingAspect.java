package org.codehaus.nanning.locking;

import org.codehaus.nanning.AspectInstance;
import org.codehaus.nanning.Invocation;
import org.codehaus.nanning.MethodInterceptor;
import org.codehaus.nanning.config.P;
import org.codehaus.nanning.config.Pointcut;

public class PessimisticLockingAspect extends SimpleMixinAspect implements Lockable {
    private boolean islocked;
    private Pointcut lockingPointcut;
    private Pointcut lockingPointcutExcludingLockable;

    public PessimisticLockingAspect(Pointcut lockingPointcut) {
        this.lockingPointcut = lockingPointcut;
        lockingPointcutExcludingLockable = P.and(this.lockingPointcut, P.not(P.isMixinInterface(Lockable.class)));
    }

    public void lock() {
        islocked = true;
    }

    public boolean isLocked() {
        return islocked;
    }

    public boolean isLocked(Lockable lockable) {
        return lockable.isLocked();
    }

    protected void doAdvise(AspectInstance aspectInstance) {
        lockingPointcutExcludingLockable.advise(aspectInstance, new MethodInterceptor() {
            public Object invoke(Invocation invocation) throws Throwable {
                if (islocked) {
                    throw new LockedException();
                }

                return invocation.invokeNext();
            }
        });
    }
}
