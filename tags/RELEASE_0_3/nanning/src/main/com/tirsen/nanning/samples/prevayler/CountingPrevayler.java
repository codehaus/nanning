package com.tirsen.nanning.samples.prevayler;

import java.io.IOException;

import junit.framework.Assert;
import org.prevayler.Transaction;
import org.prevayler.implementation.CheckpointPrevayler;

public class CountingPrevayler extends CheckpointPrevayler {
    private int numberOfCommandsInLog = 0;

    public CountingPrevayler(Object system, String dir) throws IOException, ClassNotFoundException {
        super(system, dir);
    }

    public void execute(Transaction transaction) {
        numberOfCommandsInLog++;
        super.execute(transaction);
    }

    public void checkpoint() {
        numberOfCommandsInLog = 0;
        super.checkpoint();
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
