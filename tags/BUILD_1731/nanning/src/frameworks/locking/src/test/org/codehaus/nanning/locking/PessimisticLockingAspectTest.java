package org.codehaus.nanning.locking;

import junit.framework.TestCase;
import org.codehaus.nanning.AspectInstance;
import org.codehaus.nanning.MixinInstance;
import org.codehaus.nanning.config.P;

public class PessimisticLockingAspectTest extends TestCase {
    private PessimisticLockingAspect lockAspect = new PessimisticLockingAspect(P.all());

    public static interface Interface {
        void method();
    }

    private Interface createLockAspectedObject(Interface target) {
        AspectInstance instance = createLockAspectedInstance(target);
        return (Interface) instance.getProxy();
    }

    private AspectInstance createLockAspectedInstance(Interface target) {
        AspectInstance instance = new AspectInstance();
        instance.addMixin(new MixinInstance(Interface.class, target));
        lockAspect.introduce(instance);
        lockAspect.advise(instance);
        return instance;
    }

    private boolean wasCalled;

    public void testAdvise() throws NoSuchMethodException {
        AspectInstance instance = createLockAspectedInstance(null);
        assertEquals(1, instance.getAllInterceptors().size());
        assertEquals(0, instance.getMixinForInterface(Lockable.class).getAllInterceptors().size());
    }

    public void testIntroduce() {
        AspectInstance instance = createLockAspectedInstance(null);
        assertEquals(2, instance.getMixins().size());
        MixinInstance mixin = instance.getMixinForInterface(Lockable.class);
        assertNotNull(mixin);
        assertNotNull(mixin.getTarget());

        Lockable lockable = (Lockable) instance.getProxy();
        lockable.lock();
        assertTrue(lockable.isLocked());
    }

    public void testCallWithoutLock() {
        Interface o = createLockAspectedObject(
                new Interface() {
                    public void method() {
                        wasCalled = true;
                    }
                });

        assertFalse(wasCalled);
        o.method();
        assertTrue(wasCalled);
    }

    public void testCallWithLock() {
        Interface o = createLockAspectedObject(
                new Interface() {
                    public void method() {
                        wasCalled = true;
                    }
                });

        assertFalse(wasCalled);
        ((Lockable) o).lock();
        try {
            o.method();
            fail();
        } catch (LockedException shouldHappen) {
        }
        assertFalse(wasCalled);
    }
}
