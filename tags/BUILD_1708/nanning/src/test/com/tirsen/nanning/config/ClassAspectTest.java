package com.tirsen.nanning.config;

import junit.framework.TestCase;
import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MixinInstance;

public class ClassAspectTest extends TestCase {
    private boolean wasCalled;

    public static interface Interface {}

    public static interface OtherInterface {}

    public void testAdvise() {
        ClassAspect classAspect = new ClassAspect(Interface.class);
        classAspect.addAspect(new Aspect() {
            public void introduce(AspectInstance aspectInstance) {
            }

            public void advise(AspectInstance aspectInstance) {
                wasCalled = true;
            }
        });

        assertFalse(wasCalled);
        classAspect.advise(new AspectInstance(OtherInterface.class));
        assertFalse(wasCalled);
        classAspect.advise(new AspectInstance(Interface.class));
        assertTrue(wasCalled);
    }

    public void testIntroduce() {
        ClassAspect classAspect = new ClassAspect(Interface.class);
        classAspect.addAspect(new Aspect() {
            public void introduce(AspectInstance aspectInstance) {
                wasCalled = true;
            }

            public void advise(AspectInstance aspectInstance) {
            }
        });

        assertFalse(wasCalled);
        classAspect.newInstance(OtherInterface.class);
        assertFalse(wasCalled);
        classAspect.newInstance(Interface.class);
        assertTrue(wasCalled);
    }


}
