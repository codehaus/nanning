package com.tirsen.nanning.samples.prevayler;

import java.io.IOException;

import junit.framework.Assert;
import org.prevayler.Prevayler;
import org.prevayler.Transaction;

public class CountingPrevayler extends FilterPrevayler {
    private int numberOfTransactionsInLog = 0;
    private StringBuffer transactionLog;

    public CountingPrevayler(Prevayler prevayler) {
        super(prevayler);
    }

    public void setLogTransactionMethods(boolean flag) {
        transactionLog = flag ? new StringBuffer() : null;
    }

    public void execute(Transaction transaction) {
        numberOfTransactionsInLog++;
        if (transactionLog != null && transaction instanceof InvokeCommand) {
            InvokeCommand invokeCommand = (InvokeCommand) transaction;
            if (transactionLog.length() != 0) {
                transactionLog.append(" ");
            }
            transactionLog.append(invokeCommand.getCall().getMethod().getName());
        }

        super.execute(transaction);
    }

    public void assertNumberOfCommands(String message, int expectedNumber) {
        Assert.assertEquals(message + ", wrong number of commands in log", expectedNumber, numberOfTransactionsInLog);
    }

    public void assertNumberOfCommands(int expectedNumber) {
        Assert.assertEquals("wrong number of commands in log", expectedNumber, numberOfTransactionsInLog);
    }

    public int getNumberOfTransactionsInLog() {
        return numberOfTransactionsInLog;
    }

    public void resetCount() {
        numberOfTransactionsInLog = 0;
        if (transactionLog != null) {
            transactionLog = new StringBuffer();
        }
    }

    public String getTransactionLog() {
        return transactionLog.toString();
    }
}
