package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.AspectRepository;
import com.tirsen.nanning.InterceptorDefinition;
import org.prevayler.Prevayler;

/**
 * All this stuff should actually be thread-local instead, that way you can be running to several prevaylers at the
 * same time.
 */
public class CurrentPrevayler {
    private static AspectRepository aspectRepository;
    private static IdentifyingSystem currentSystem;
    private static Prevayler prevayler;

    public static AspectRepository getAspectRepository() {
        return aspectRepository;
    }

    public static void setAspectRepository(AspectRepository aspectRepository) {
        CurrentPrevayler.aspectRepository = aspectRepository;
    }

    public static IdentifyingSystem getSystem() {
        return currentSystem;
    }

    public static void setSystem(IdentifyingSystem system) {
        CurrentPrevayler.currentSystem = system;
    }

    public static Prevayler getPrevayler() {
        return prevayler;
    }

    public static void setPrevayler(Prevayler prevayler) {
        CurrentPrevayler.prevayler = prevayler;
        CurrentPrevayler.setSystem((IdentifyingSystem) prevayler.system());
    }

    public static PrevaylerInterceptor getPrevaylerInterceptor() {
        InterceptorDefinition interceptorDefinition = aspectRepository.getInterceptor(PrevaylerInterceptor.class);
        PrevaylerInterceptor prevaylerInterceptor = (PrevaylerInterceptor) interceptorDefinition.getSingleton();
        return prevaylerInterceptor;
    }
}
