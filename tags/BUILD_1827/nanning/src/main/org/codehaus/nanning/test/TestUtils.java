package org.codehaus.nanning.test;

import java.util.LinkedList;
import java.util.List;

public class TestUtils {

    public static void gc() {
        byte[] bah = new byte[(int) Runtime.getRuntime().freeMemory()];
        System.gc();
        bah = null;
    }
}
