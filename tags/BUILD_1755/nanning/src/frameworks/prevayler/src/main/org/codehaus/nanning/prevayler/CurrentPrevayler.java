package org.codehaus.nanning.prevayler;

import org.prevayler.Prevayler;

import java.util.Stack;

public class CurrentPrevayler {
    private static ThreadLocal currentPrevayler = new InheritableThreadLocal();
    private static ThreadLocal currentSystems = new ThreadLocal();

    /**
     * @deprecated is this used by anyone?
     */
    public static boolean isInitialized() {
        return hasPrevayler();
    }

    public static Object getSystem() {
        Object system;
        if (isInTransaction()) {
            system = currentSystems().peek();
        } else {
            system = getPrevayler().prevalentSystem();
        }
        assert system != null : "Prevayler not initialized for this thread, no current system";
        return system;
    }

    private static Stack currentSystems() {
        Stack stack = (Stack) currentSystems.get();
        if (stack == null) {
            currentSystems.set(stack = new Stack());
        }
        return stack;
    }

    public static Prevayler getPrevayler() {
        Prevayler prevayler = (Prevayler) currentPrevayler.get();
        assert prevayler != null : "Prevayler not initialized for this thread, no current Prevayler";
        return prevayler;
    }

    public static Object clockedSystem() {
        return  getSystem();
    }

    public static void setPrevayler(Prevayler prevayler) {
        currentPrevayler.set(prevayler);
    }

    public static boolean isReplaying() {
        return getSystem() != null && getPrevayler() == null;
    }

    public static void enterTransaction(Object system) {
        currentSystems().push(system);
    }

    public static void exitTransaction() {
        assert isInTransaction() : "not in transaction";
        currentSystems().pop();
    }

    public static boolean isInTransaction() {
        return !currentSystems().empty();
    }

    public static void withPrevayler(Prevayler prevayler, final Runnable runnable) {
        try {
            withPrevayler(prevayler, new PrevaylerAction() {
                public Object run() throws Exception {
                    runnable.run();
                    return null;
                }
            });
        } catch (Error e) {
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object withPrevayler(Prevayler prevayler, PrevaylerAction action) throws Exception {
        Prevayler lastPrevayler = (Prevayler) currentPrevayler.get();
        setPrevayler(prevayler);
        try {
            return action.run();
        } finally {
            setPrevayler(lastPrevayler);
        }
    }

    public static boolean hasPrevayler() {
        return currentPrevayler.get() != null;
    }

}
