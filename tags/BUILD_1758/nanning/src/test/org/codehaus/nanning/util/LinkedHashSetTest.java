package org.codehaus.nanning.util;

import java.util.*;

import junit.framework.TestCase;

public class LinkedHashSetTest extends TestCase {
    private LinkedHashSet linkedHashSet;

    protected void setUp() throws Exception {
        super.setUp();
        linkedHashSet = new LinkedHashSet();
    }

    public void testDefaultInitialization() throws Exception {
        assertTrue(linkedHashSet.isEmpty());
        assertTrue(linkedHashSet.size() == 0);
        assertFalse(linkedHashSet.iterator().hasNext());
    }

    public void testMultipleAdd() throws Exception {
        assertTrue(linkedHashSet.isEmpty());
        linkedHashSet.add("a");
        assertEquals(1, linkedHashSet.size());
        linkedHashSet.add("a");
        assertEquals(1, linkedHashSet.size());
    }

    public void testIterationOrder() throws Exception {
        linkedHashSet.add("1");
        linkedHashSet.add("2");
        linkedHashSet.add("3");
        Iterator iterator = linkedHashSet.iterator();
        assertEquals("1", iterator.next());
        assertEquals("2", iterator.next());
        assertEquals("3", iterator.next());

        linkedHashSet.clear();

        linkedHashSet.add("3");
        linkedHashSet.add("2");
        linkedHashSet.add("1");
        iterator = linkedHashSet.iterator();
        assertEquals("3", iterator.next());
        assertEquals("2", iterator.next());
        assertEquals("1", iterator.next());
    }
}
