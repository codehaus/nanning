package org.codehaus.nanning.prevayler;

import org.prevayler.*;

import java.io.IOException;

public class FilterPrevayler implements Prevayler {
    protected Prevayler prevayler;

    public FilterPrevayler(Prevayler prevayler) {
        this.prevayler = prevayler;
    }

    public Prevayler getWrappedPrevayler() {
        return prevayler;
    }

    public void execute(Transaction transaction) {
        prevayler.execute(transaction);
    }

    public Object prevalentSystem() {
        return prevayler.prevalentSystem();
    }

    public Clock clock() {
        return prevayler.clock();
    }

    public Object execute(Query sensitiveQuery) throws Exception {
        return prevayler.execute(sensitiveQuery);
    }

    public Object execute(TransactionWithQuery transactionWithQuery) throws Exception {
        return prevayler.execute(transactionWithQuery);
    }

    public void takeSnapshot() throws IOException {
        prevayler.takeSnapshot();
    }
}
