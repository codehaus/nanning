package com.tirsen.nanning.samples.prevayler;

import java.io.IOException;

import junit.framework.Assert;
import org.prevayler.Prevayler;
import org.prevayler.Transaction;

public class CountingPrevayler extends FilterPrevayler {
    private int numberOfCommandsInLog = 0;

    public CountingPrevayler(Prevayler prevayler) {
        super(prevayler);
    }

    public void execute(Transaction transaction) {
        numberOfCommandsInLog++;
        super.execute(transaction);
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
