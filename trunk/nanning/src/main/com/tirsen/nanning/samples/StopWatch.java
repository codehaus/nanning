package com.tirsen.nanning.samples;

public class StopWatch {
    ///CLOVER:OFF

    private long startMemory;
    private long startTime;
    private long time;
    private long memory;
    private static final double MILLIS_PER_SECOND = 1000;
    private boolean stopped = false;
    private static final int BYTES_PER_K = 1024;

    public StopWatch() {
        this(false);
    }

    public StopWatch(boolean doGC) {
        if (doGC) {
            System.gc();
            System.gc();
            System.gc();
        }
        startMemory = Runtime.getRuntime().freeMemory();
        startTime = System.currentTimeMillis();
    }

    public void stop() {
        stopped = true;
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
        assert stopped : "you need to invoke stop() first";
        return getTimeSpent() / (double) numberOfIterations;
    }

    public double getMemoryUsed(int numberOfIterations) {
        assert stopped : "you need to invoke stop() first";
        return getMemoryUsed() / (double) numberOfIterations;
    }

    public String getTimeSpentSeconds() {
        assert stopped : "you need to invoke stop() first";
        return getTimeSpent() / MILLIS_PER_SECOND + "s";
    }

    public String getMemoryUsedKs() {
        assert stopped : "you need to invoke stop() first";
        return getMemoryUsed() / BYTES_PER_K + "k";
    }
    ///CLOVER:ON
}
