package org.codehaus.nanning.prevayler;

import org.codehaus.nanning.Aspects;
import org.codehaus.nanning.prevayler.CheckTransactionUnsupportedInterceptor;
import org.codehaus.nanning.prevayler.TestUnsupportedTransaction;
import junit.framework.Assert;

public class TestUnsupportedTransactionImpl implements TestUnsupportedTransaction {
    public void callWithUnsupportedTransaction() {
        Assert.assertFalse(CheckTransactionUnsupportedInterceptor.isTransactionsSupported());
        MyObject myObject = (MyObject) Aspects.getCurrentAspectFactory().newInstance(MyObject.class);
        myObject.setValue("test"); // this call should not be permitted
    }
}
