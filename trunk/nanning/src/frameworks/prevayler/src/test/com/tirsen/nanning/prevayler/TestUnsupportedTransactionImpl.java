package com.tirsen.nanning.prevayler;

import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.prevayler.CheckTransactionUnsupportedInterceptor;
import com.tirsen.nanning.prevayler.TestUnsupportedTransaction;
import junit.framework.Assert;

public class TestUnsupportedTransactionImpl implements TestUnsupportedTransaction {
    public void callWithUnsupportedTransaction() {
        Assert.assertFalse(CheckTransactionUnsupportedInterceptor.isTransactionsSupported());
        MyObject myObject = (MyObject) Aspects.getCurrentAspectFactory().newInstance(MyObject.class);
        myObject.setValue("test"); // this call should not be permitted
    }
}
