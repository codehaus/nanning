package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.Aspects;
import junit.framework.Assert;

public class TestUnsupportedTransactionImpl implements TestUnsupportedTransaction {
    public void callWithUnsupportedTransaction() {
        Assert.assertFalse(CheckTransactionUnsupportedInterceptor.isTransactionsSupported());
        MyObject myObject = (MyObject) Aspects.getCurrentAspectFactory().newInstance(MyObject.class);
        myObject.setValue("test"); // this call should not be permitted
    }
}
