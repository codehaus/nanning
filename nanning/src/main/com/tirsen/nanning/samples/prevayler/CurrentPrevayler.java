package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.definition.InterceptorDefinition;
import org.prevayler.Prevayler;
import org.prevayler.PrevalentSystem;

import java.util.Stack;

/**
 * All this stuff should actually be thread-local instead, that way you can be running to several prevaylers at the
 * same time.
 */
public class CurrentPrevayler {
    private static ThreadLocal isInTransaction = new ThreadLocal() {
        protected Object initialValue() {
            return new Integer(0);
        }
    };
    private static ThreadLocal currentPrevayler = new InheritableThreadLocal();
    private static ThreadLocal currentSystem = new InheritableThreadLocal();

    public static IdentifyingSystem getSystem() {
        IdentifyingSystem identifyingSystem = (IdentifyingSystem) currentSystem.get();
        assert identifyingSystem != null : "Prevayler not initialized for this thread, no current system";
        return identifyingSystem;
    }

    public static void setSystem(IdentifyingSystem system) {
        currentSystem.set(system);
    }

    public static Prevayler getPrevayler() {
        Prevayler prevayler = (Prevayler) currentPrevayler.get();
        assert prevayler != null : "Prevayler not initialized for this thread, no current Prevayler";
        return prevayler;
    }

    public static void setPrevayler(Prevayler prevayler) {
        currentPrevayler.set(prevayler);
        setSystem((IdentifyingSystem) prevayler.system());
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
}
