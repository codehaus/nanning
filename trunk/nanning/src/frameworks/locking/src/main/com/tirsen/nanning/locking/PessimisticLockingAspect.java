package com.tirsen.nanning.locking;

import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.MethodInterceptor;
import com.tirsen.nanning.MixinInstance;
import com.tirsen.nanning.config.AllPointcut;
import com.tirsen.nanning.config.Pointcut;

public class PessimisticLockingAspect extends SimpleMixinAspect implements Lockable {
    private boolean islocked;
    private Pointcut lockingPointcut;

    public PessimisticLockingAspect() {
    }

    public PessimisticLockingAspect(Pointcut lockingPointcut) {
        this.lockingPointcut = lockingPointcut;
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

    /**
     * Override to change methods that requires lock-checks in a subclass.
     * @return Pointcut defining what methods require lock-checks.
     */
    protected Pointcut lockingPointcut() {
        return lockingPointcut;
    }

    protected void doAdvise(AspectInstance aspectInstance) {
        // remove methods in Locking-mixin, otherwise it won't be possible to unlock a locked object 
        Pointcut pointcut =
                P.and(lockingPointcut(),
                        new AllPointcut() {
                            public boolean adviseMixin(MixinInstance mixin) {
                                return mixin.getInterfaceClass() != Lockable.class;
                            }
                        });

        pointcut.advise(aspectInstance, new MethodInterceptor() {
            public Object invoke(Invocation invocation) throws Throwable {
                if (islocked) {
                    throw new LockedException();
                }

                return invocation.invokeNext();
            }
        });
    }
}
