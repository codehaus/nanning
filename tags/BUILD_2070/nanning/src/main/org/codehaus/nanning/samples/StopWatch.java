package org.codehaus.nanning.samples;

import org.codehaus.nanning.AssertionException;

public class StopWatch {
    ///CLOVER:OFF

    public static final double MILLIS_PER_SECOND = 1000;
    public static final int BYTES_PER_K = 1024;
    
    private long startMemory;
    private long startTime;
    private long time;
    private long memory;
    private boolean stopped = false;

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
        assertStopped();
        return getTimeSpent() / (double) numberOfIterations;
    }

    public double getMemoryUsed(int numberOfIterations) {
        assertStopped();
        return getMemoryUsed() / (double) numberOfIterations;
    }

    public String getTimeSpentSeconds() {
        assertStopped();
        return getTimeSpent() / MILLIS_PER_SECOND + "s";
    }

    public String getMemoryUsedKs() {
        assertStopped();
        return getMemoryUsed() / BYTES_PER_K + "k";
    }

    private void assertStopped() {
        if (!stopped) {
            throw new AssertionException("you need to invoke stop() first");
        }
    }
    ///CLOVER:ON
}
