package com.tirsen.nanning.samples.prevayler;

import java.util.ArrayList;
import java.util.Date;

import com.tirsen.nanning.attribute.AbstractAttributesTest;
import com.tirsen.nanning.config.AspectSystem;
import com.tirsen.nanning.config.FindTargetMixinAspect;

import com.tirsen.nanning.samples.prevayler.BasicIdentifyingSystem;

public class IdentifyingMarshallerTest extends AbstractAttributesTest {
    private IdentifyingMarshaller marshaller;
    private long entityId;
    private MyObject entity;
    private AspectSystem aspectSystem;
    private TestSystem system;

    protected void setUp() throws Exception {
        super.setUp();
        aspectSystem = new AspectSystem();
        aspectSystem.addAspect(new FindTargetMixinAspect());

        system = new TestSystem();
        entity = (MyObject) aspectSystem.newInstance(MyObject.class);
        system.setEntity(entity);
        CurrentPrevayler.enterTransaction();
        entityId = system.registerObjectID(entity);
        CurrentPrevayler.exitTransaction();

        marshaller = new IdentifyingMarshaller();
        CurrentPrevayler.setSystem(system);
    }

    protected void tearDown() throws Exception {
        CurrentPrevayler.setSystem(null);
        while (CurrentPrevayler.isInTransaction()) {
            CurrentPrevayler.exitTransaction();
        }
        super.tearDown();
    }

    public void testMarshalNull() {
        testMarshalAndUnMarshalPrimitive(null);
    }

    public void testMarshalString() {
        testMarshalAndUnMarshalPrimitive("blahonga");
    }

    public void testMarshalInt() {
        testMarshalAndUnMarshalPrimitive(new Integer(5));
    }

    public void testMarshalDate() {
        testMarshalAndUnMarshalPrimitive(new Date());
    }

    public void testMarshalList() {
        testMarshalAndUnMarshalPrimitive(new ArrayList());
    }

    public void testMarshalRegisteredEntity() {
        Identity identity = (Identity) marshaller.marshal(entity);
        assertEquals("Marshaller generated invalid id", entityId, ((Long) identity.getIdentifier()).longValue());
        assertEquals("Wrong class in identity", MyObject.class, identity.getObjectClass());

        assertSame("Entity unmarshalled incorrectly", entity, marshaller.unmarshal(identity));
    }

    public void testMarshalUnregisteredEntity() {
        Object object = aspectSystem.newInstance(MyObject.class);
        assertSame("Unregistered entity should be marshalled by value", object, marshaller.marshal(object));
        assertFalse(system.hasObjectID(object));
        CurrentPrevayler.enterTransaction();
        assertSame("Unregistered entity should be marshalled by value", object, marshaller.unmarshal(object));
        CurrentPrevayler.exitTransaction();
        assertTrue(system.hasObjectID(object));
    }

    public void testUnmarshalUnregisteredGraph() {
        MyObject object1 = (MyObject) aspectSystem.newInstance(MyObject.class);
        MyObject object2 = (MyObject) aspectSystem.newInstance(MyObject.class);
        object1.setMyObject(object2);

        marshaller.marshal(object1);
        assertFalse(system.hasObjectID(object1));
        assertFalse(system.hasObjectID(object2));

        CurrentPrevayler.enterTransaction();
        marshaller.unmarshal(object1);
        CurrentPrevayler.exitTransaction();
        assertTrue(system.hasObjectID(object1));
        assertTrue(system.hasObjectID(object2));
    }

    public void testUnmarshalMixedGraph() {
        MyObject object1 = (MyObject) aspectSystem.newInstance(MyObject.class);
        MyObject object2 = (MyObject) aspectSystem.newInstance(MyObject.class);
        object1.setMyObject(object2);

        CurrentPrevayler.enterTransaction();
        system.registerObjectID(object2);

        marshaller.marshal(object1);
        assertFalse(system.hasObjectID(object1));
        assertTrue(system.hasObjectID(object2));

        try {
            marshaller.unmarshal(object1);
            fail("Should not be able to unmarshal a partially registered graph");

        } catch (AssertionError shouldHappen) {
        }
    }

    public static class TestSystem extends BasicIdentifyingSystem {
        private MyObject entity;

        public MyObject getEntity() {
            return entity;
        }

        public void setEntity(MyObject entity) {
            this.entity = entity;
        }
    }

    private void testMarshalAndUnMarshalPrimitive(Object object) {
        assertSame("A non entity should not be identified", object, marshaller.marshal(object));
        assertSame("A non entity should not be identified", object, marshaller.unmarshal(object));
        assertFalse("Primitive should not become registered", system.hasObjectID(object));
    }
}
