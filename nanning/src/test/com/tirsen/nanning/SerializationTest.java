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

            public Object newInstance(Class classIdentifier, Object[] targets) {
                AspectInstance aspectInstance = createSingleMixin();
                aspectInstance.setTarget(Intf.class, targets[0]);
                return aspectInstance.getProxy();
            }
        };

        AspectFactory multipleAspectFactory = new AspectFactory() {
            public Object newInstance(Class classIdentifier) {
                return createMultiMixin().getProxy();
            }

            public Object newInstance(Class classIdentifier, Object[] targets) {
                AspectInstance aspectInstance = createMultiMixin();
                aspectInstance.setTarget(Intf.class, targets[0]);
                if (targets.length > 1) {
                    aspectInstance.setTarget(TestMixin.class, targets[1]);
                }
                return aspectInstance.getProxy();
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
        assertEquals(2, Aspects.getAspectInstance(serialized).getMixins().size());
        // unfortunately added mixins does not (yet?) work, proxies are not recreated on serialization... :-(
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
        addInterceptor(aspectInstance);
        return aspectInstance;
    }

    private AspectInstance createSingleMixin() {
        AspectInstance aspectInstance = new AspectInstance(Intf.class);
        MixinInstance mixinInstance = new MixinInstance();
        mixinInstance.setInterfaceClass(Intf.class);
        mixinInstance.setTarget(new Impl());
        aspectInstance.addMixin(mixinInstance);
        addInterceptor(aspectInstance);
        return aspectInstance;
    }

    private void addInterceptor(AspectInstance aspectInstance) {
        Collection mixins = aspectInstance.getMixins();
        for (Iterator iterator = mixins.iterator(); iterator.hasNext();) {
            MixinInstance mixinInstance = (MixinInstance) iterator.next();
            mixinInstance.addInterceptor(new NullInterceptor());
        }
    }
}
