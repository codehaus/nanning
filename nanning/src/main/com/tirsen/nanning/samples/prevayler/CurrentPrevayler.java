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
    private static ThreadLocal currentPrevayler = new ThreadLocal() {
        protected Object initialValue() {
            return new Stack();
        }
    };

    public static IdentifyingSystem getSystem() {
        return (IdentifyingSystem) ((Stack) currentPrevayler.get()).peek();
    }

    public static void setSystem(IdentifyingSystem system) {
        PrevaylerInterceptor.getPrevaylerInterceptor().setSystem(system);
    }

    public static Prevayler getPrevayler() {
        return PrevaylerInterceptor.getPrevaylerInterceptor().getPrevayler();
    }

    public static void setPrevayler(Prevayler prevayler) {
        PrevaylerInterceptor.getPrevaylerInterceptor().setPrevayler(prevayler);
    }

    public static void enterCommand(IdentifyingSystem prevalentSystem) {
        ((Stack) currentPrevayler.get()).push(prevalentSystem);
    }

    public static void exitCommand() {
        ((Stack) currentPrevayler.get()).pop();
    }

    public static boolean isInCommand() {
        return !((Stack) currentPrevayler.get()).isEmpty();
    }
}
