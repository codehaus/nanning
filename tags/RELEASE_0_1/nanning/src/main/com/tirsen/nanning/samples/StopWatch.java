package com.tirsen.nanning.samples;

public class StopWatch {
    ///CLOVER:OFF

    private long startMemory;
    private long startTime;
    private long time;
    private long memory;

    public StopWatch() {
        this(true);
    }

    public StopWatch(boolean doGC) {
        if (doGC) {
            System.gc(); System.gc(); System.gc();
        }
        startMemory = Runtime.getRuntime().freeMemory();
        startTime = System.currentTimeMillis();
    }

    public void stop() {
        time = System.currentTimeMillis() - startTime;
        memory = startMemory - Runtime.getRuntime().freeMemory();
    }

    public double getTimeSpent() {
        return time;
    }

    public double getMemoryUsed() {
        return memory;
    }

    public double getTimeSpent(int numberOfIterations) {
        return getTimeSpent() / (double) numberOfIterations;
    }

    public double getMemoryUsed(int numberOfIterations) {
        return getMemoryUsed() / (double) numberOfIterations;
    }
    ///CLOVER:ON
}
