package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.InterceptorDefinition;
import org.prevayler.Prevayler;

/**
 * All this stuff should actually be thread-local instead, that way you can be running to several prevaylers at the
 * same time.
 */
public class CurrentPrevayler {
    public static IdentifyingSystem getSystem() {
        return getPrevaylerInterceptor().getSystem();
    }

    public static void setSystem(IdentifyingSystem system) {
        getPrevaylerInterceptor().setSystem(system);
    }

    public static Prevayler getPrevayler() {
        return getPrevaylerInterceptor().getPrevayler();
    }

    public static void setPrevayler(Prevayler prevayler) {
        getPrevaylerInterceptor().setPrevayler(prevayler);
    }

    public static PrevaylerInterceptor getPrevaylerInterceptor() {
        InterceptorDefinition interceptorDefinition =
                Aspects.getCurrentAspectRepository().getInterceptor(PrevaylerInterceptor.class);
        PrevaylerInterceptor prevaylerInterceptor = (PrevaylerInterceptor) interceptorDefinition.getSingleton();
        return prevaylerInterceptor;
    }
}
