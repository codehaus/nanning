package com.tirsen.nanning.samples.prevayler;

import java.io.*;

import junit.framework.TestCase;

import com.tirsen.nanning.test.TestUtils;

import com.tirsen.nanning.samples.prevayler.SoftMap;
import com.tirsen.nanning.config.AspectSystem;
import com.tirsen.nanning.config.FindTargetMixinAspect;
import com.tirsen.nanning.Aspects;

public class SoftMapTest extends TestCase {
    private String smallObject;
    private String largeObject;

    protected void setUp() throws Exception {
        super.setUp();

        smallObject = "key";
        largeObject = "hej";
        for (int i = 0; i < 400; i++) {
            largeObject += "hej";
        }
    }

    public void testSoftValues() throws InterruptedException {

        SoftMap map = SoftMap.createSoftValuesMap();
        map.put(smallObject, largeObject);

        assertEquals(largeObject, map.get(smallObject));
        largeObject = null;

        TestUtils.gc();

        assertNull("Value was not removed after GC", map.get(smallObject));
    }

    public void testSoftKeys() throws InterruptedException {

        SoftMap map = SoftMap.createSoftKeysMap();
        map.put(largeObject, smallObject);

        assertEquals(smallObject, map.get(largeObject));
        largeObject = null;

        TestUtils.gc();

        assertNull("Value was not removed after GC", map.get(largeObject));
    }

    public void testSerializationOfSoftValues() throws IOException, ClassNotFoundException {

        SoftMap map = SoftMap.createSoftValuesMap();
        map.put(smallObject, largeObject);

        ByteArrayOutputStream data = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(data);
        out.writeObject(map);
        out.close();

        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data.toByteArray()));
        SoftMap readMap = (SoftMap) in.readObject();
        in.close();
        in = null;
        out = null;

        assertEquals(largeObject, readMap.get(smallObject));
        largeObject = null;

        TestUtils.gc();
        assertNull("Value was not removed after serialization and GC", readMap.get(smallObject));
    }

    public void testSerializationOfSoftKeys() throws IOException, ClassNotFoundException {

        SoftMap map = SoftMap.createSoftKeysMap();
        map.put(largeObject, smallObject);

        ByteArrayOutputStream data = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(data);
        out.writeObject(map);
        out.close();

        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data.toByteArray()));
        SoftMap readMap = (SoftMap) in.readObject();
        in.close();
        in = null;
        out = null;

        assertEquals(smallObject, readMap.get(largeObject));
        largeObject = null;

        TestUtils.gc();
        assertNull("Value was not removed after serialization and GC", readMap.get(largeObject));
    }
    
    public void testSerializationWithAspects() throws IOException, ClassNotFoundException {
        AspectSystem system = new AspectSystem();
        Aspects.setContextAspectFactory(system);
        system.addAspect(new FindTargetMixinAspect());
        Object o = system.newInstance(MyObject.class);

        SoftMap map = SoftMap.createSoftKeysMap();
        map.put(o, "miffo");
        
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(data);
        out.writeObject(map);
        out.close();

        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data.toByteArray()));
        SoftMap readMap = (SoftMap) in.readObject();
        in.close();
        in = null;
        out = null;

        assertEquals("miffo", readMap.get(readMap.keySet().iterator().next()));
    }
}