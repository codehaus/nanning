package com.tirsen.nanning.samples.prevayler;

import org.prevayler.Prevayler;
import org.prevayler.util.clock.ClockedSystem;

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

    public static Object getSystem() {
        Object system;
        if (hasPrevayler()) {
            system = getPrevayler().prevalentSystem();
        } else {
            system = currentSystem.get();
        }
        assert system != null : "Prevayler not initialized for this thread, no current system";
        return system;
    }

    public static void setSystem(Object system) {
        currentSystem.set(system);
        if (system != null) {
            setPrevayler(null);
        }
    }

    public static Prevayler getPrevayler() {
        Prevayler prevayler = (Prevayler) currentPrevayler.get();
        assert prevayler != null : "Prevayler not initialized for this thread, no current Prevayler";
        return prevayler;
    }

    public static ClockedSystem clockedSystem() {
        return (ClockedSystem) getSystem();
    }

    public static void setPrevayler(Prevayler prevayler) {
        currentPrevayler.set(prevayler);
        if (prevayler != null) {
            setSystem(null);
        }
    }

    public static boolean isReplaying() {
        return getSystem() != null && getPrevayler() == null;
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

    public static boolean hasSystem() {
        return hasPrevayler() || currentSystem.get() != null;
    }
}
