package com.tirsen.nanning.samples;

public class StopWatch {
    private long startTime;
    private long endTime;

    public StopWatch() {
    }

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public void stop() {
        endTime = System.currentTimeMillis();
    }

    public long getTime() {
        return endTime - startTime;
    }
}
