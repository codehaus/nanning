package com.tirsen.nanning.samples.prevayler;

import org.prevayler.Prevayler;

public class CurrentPrevayler {
    private static ThreadLocal isInTransaction = new ThreadLocal() {
        protected Object initialValue() {
            return new Integer(0);
        }
    };
    private static ThreadLocal currentPrevayler = new InheritableThreadLocal();
    private static ThreadLocal currentSystem = new InheritableThreadLocal();

    public static boolean isInitialized() {
        return currentSystem.get() != null;
    }

    public static IdentifyingSystem getSystem() {
        IdentifyingSystem identifyingSystem = (IdentifyingSystem) currentSystem.get();
        assert identifyingSystem != null : "Prevayler not initialized for this thread, no current system";
        return identifyingSystem;
    }

    static void setSystem(IdentifyingSystem system) {
        currentSystem.set(system);
    }

    public static Prevayler getPrevayler() {
        Prevayler prevayler = (Prevayler) currentPrevayler.get();
        assert prevayler != null : "Prevayler not initialized for this thread, no current Prevayler";
        return prevayler;
    }

    public static void setPrevayler(Prevayler prevayler) {
        currentPrevayler.set(prevayler);
        setSystem(prevayler == null ? null : (IdentifyingSystem) prevayler.system());
    }

    static boolean isReplaying() {
        return getSystem() != null;
    }

    public static void enterTransaction() {
        isInTransaction.set(new Integer(transactionCount() + 1));
    }

    public static void exitTransaction() {
        assert isInTransaction() : "not in transaction";
        isInTransaction.set(new Integer(transactionCount() - 1));
    }

    public static boolean isInTransaction() {
        return transactionCount() != 0;
    }

    private static int transactionCount() {
        return ((Integer) isInTransaction.get()).intValue();
    }

    public static void withPrevayler(Prevayler prevayler, final Runnable runnable) {
        try {
            withPrevayler(prevayler, new PrevaylerAction() {
                public Object run() throws Exception {
                    runnable.run();
                    return null;
                }
            });
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
