package com.tirsen.nanning;

import java.io.*;
import java.util.Collection;
import java.util.Iterator;

import com.tirsen.nanning.attribute.AbstractAttributesTest;

public class SerializationTest extends AbstractAttributesTest {
    public void testChangedAspectFactoryBetweenSerializations() throws IOException, ClassNotFoundException {
        AspectFactory singleAspectFactory = new AspectFactory() {
            public Object newInstance(Class classIdentifier) {
                return createSingleMixin().getProxy();
            }

            public void reinitialize(AspectInstance aspectInstance) {
                addInterceptors(aspectInstance, 1);
            }
        };

        AspectFactory multipleAspectFactory = new AspectFactory() {
            public Object newInstance(Class classIdentifier) {
                return createMultiMixin().getProxy();
            }

            public void reinitialize(AspectInstance aspectInstance) {
                addInterceptors(aspectInstance, 3);
            }
        };

        Aspects.setContextAspectFactory(singleAspectFactory);
        Intf intf = (Intf) singleAspectFactory.newInstance(Intf.class);
        Object serialized = serializeObject(intf);
        assertTrue(serialized instanceof Intf);
        assertEquals(1, Aspects.getAspectInstance(serialized).getAllInterceptors().size());
        assertEquals(1, Aspects.getAspectInstance(serialized).getMixins().size());

        Aspects.setContextAspectFactory(multipleAspectFactory);
        serialized = serializeObject(intf);
        assertTrue(serialized instanceof Intf);
        assertEquals(3, Aspects.getAspectInstance(serialized).getAllInterceptors().size());

        // unfortunately added mixins does not (yet?) work :-(
        assertEquals(1, Aspects.getAspectInstance(serialized).getMixins().size());
        assertFalse(serialized instanceof TestMixin);
    }

    private Object serializeObject(Intf intf) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(intf);
        return new ObjectInputStream(new ByteArrayInputStream(outputStream.toByteArray())).readObject();
    }

    private AspectInstance createMultiMixin() {
        AspectInstance aspectInstance = createSingleMixin();
        MixinInstance mixinInstance = new MixinInstance();
        mixinInstance.setInterfaceClass(TestMixin.class);
        mixinInstance.setTarget(new TestMixinImpl());
        aspectInstance.addMixin(mixinInstance);
        addInterceptors(aspectInstance, 1);
        return aspectInstance;
    }

    private AspectInstance createSingleMixin() {
        AspectInstance aspectInstance = new AspectInstance(Intf.class);
        MixinInstance mixinInstance = new MixinInstance();
        mixinInstance.setInterfaceClass(Intf.class);
        mixinInstance.setTarget(new IntfImpl());
        aspectInstance.addMixin(mixinInstance);
        addInterceptors(aspectInstance, 3);
        return aspectInstance;
    }

    private void addInterceptors(AspectInstance aspectInstance, int numberOfInterceptors) {
        Collection mixins = aspectInstance.getMixins();
        for (Iterator iterator = mixins.iterator(); iterator.hasNext();) {
            MixinInstance mixinInstance = (MixinInstance) iterator.next();
            for (int i = 0; i < numberOfInterceptors; i++) {
                mixinInstance.addInterceptor(new NullInterceptor());
            }
        }
    }
}
