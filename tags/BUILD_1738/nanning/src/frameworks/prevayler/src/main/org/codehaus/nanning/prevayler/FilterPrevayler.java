package org.codehaus.nanning.prevayler;

import org.prevayler.Prevayler;
import org.prevayler.Transaction;

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
}
