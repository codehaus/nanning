package com.tirsen.nanning.samples.prevayler;

import java.io.IOException;
import java.io.Serializable;

import org.prevayler.Command;
import junit.framework.Assert;

public class CountingPrevayler extends GarbageCollectingPrevayler {
    private int numberOfCommandsInLog = 0;

    public CountingPrevayler(IdentifyingSystem system, String dir) throws IOException, ClassNotFoundException {
        super(system, dir);
    }

    public Serializable executeCommand(Command command) throws Exception {
        numberOfCommandsInLog++;
        return super.executeCommand(command);
    }

    public void takeSnapshot() throws IOException {
        numberOfCommandsInLog = 0;
        super.takeSnapshot();
    }

    public void assertNumberOfCommands(String message, int expectedNumber) {
        Assert.assertEquals(message + ", wrong number of commands in log", expectedNumber, numberOfCommandsInLog);
    }

    public void assertNumberOfCommands(int expectedNumber) {
        Assert.assertEquals("wrong number of commands in log", expectedNumber, numberOfCommandsInLog);
    }

    public int getNumberOfCommandsInLog() {
        return numberOfCommandsInLog;
    }
}
