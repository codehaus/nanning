package org.codehaus.nanning.prevayler;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import junit.framework.Assert;
import org.prevayler.Prevayler;
import org.prevayler.Transaction;
import org.prevayler.TransactionWithQuery;

public class CountingPrevayler extends FilterPrevayler {
    private List transactions = new ArrayList();

    public CountingPrevayler(Prevayler prevayler) {
        super(prevayler);
    }

    public void execute(Transaction transaction) {
        transactions.add(transaction);
        super.execute(transaction);
    }

    public void assertNumberOfCommands(String message, int expectedNumber) {
        Assert.assertEquals(message + ", wrong number of commands in log", expectedNumber, numberOfTransactionsInLog());
    }

    private int numberOfTransactionsInLog() {
        return transactions.size();
    }

    public void assertNumberOfCommands(int expectedNumber) {
        Assert.assertEquals("wrong number of commands in log", expectedNumber, numberOfTransactionsInLog());
    }

    public int getNumberOfTransactionsInLog() {
        return numberOfTransactionsInLog();
    }

    public void reset() {
        transactions.clear();
    }

    public String getTransactionLog() {
        StringBuffer transactionLog = new StringBuffer();
        for (Iterator iterator = transactions.iterator(); iterator.hasNext();) {
            Object transaction = iterator.next();
            if (transaction instanceof InvokeTransaction) {
                InvokeTransaction invokeTransaction = (InvokeTransaction) transaction;
                if (transactionLog.length() != 0) {
                    transactionLog.append(" ");
                }
                transactionLog.append(invokeTransaction.getMethodName());
            }
        }
        return transactionLog.toString();
    }

    public Object execute(TransactionWithQuery transactionWithQuery) throws Exception {
        transactions.add(transactionWithQuery);
        return super.execute(transactionWithQuery);
    }

    public void assertTransactionLog(String message, String expectedLog) {
        Assert.assertEquals(message, expectedLog, getTransactionLog());
    }

    public void assertTransactionLog(String expectedLog) {
        assertTransactionLog("expected transactions was not executed", expectedLog);
    }

    public Object getTransaction(int index) {
        return transactions.get(index);
    }
}
