package com.tirsen.nanning.test;

import java.util.LinkedList;
import java.util.List;

public class TestUtils {
    public static void gc() {
        List list = new LinkedList();

        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        while (totalMemory == runtime.totalMemory()) {
            list.add("mongo bongo");
            if (list.size() % 400 == 0) {
                System.gc();
            }
        }
    }
}
