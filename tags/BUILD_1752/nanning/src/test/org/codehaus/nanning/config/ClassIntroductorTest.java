package org.codehaus.nanning.config;

import junit.framework.TestCase;
import org.codehaus.nanning.AspectInstance;

public class ClassIntroductorTest extends TestCase {
    public static interface Interface {}
    public static class Implementation implements Interface {}

    public static interface OtherInterface {}
    
    public void testIntroduce() {
        MixinAspect aspect = new MixinAspect(Interface.class, Implementation.class);
        AspectInstance aspectInstance = new AspectInstance(Interface.class);
        aspect.introduce(aspectInstance);
        assertEquals(1, aspectInstance.getMixins().size());
    }
    
    public void testNotIntroduce() {
        MixinAspect aspect = new MixinAspect(Interface.class, Implementation.class);
        AspectInstance aspectInstance = new AspectInstance(OtherInterface.class);
        aspect.introduce(aspectInstance);
        assertEquals(0, aspectInstance.getMixins().size());
    }
}
