package com.tirsen.nanning.samples.prevayler;

import java.util.Date;
import java.io.ObjectOutputStream;
import java.io.IOException;

import org.prevayler.util.clock.ClockedSystem;
import org.prevayler.util.TransactionWithQuery;
import org.prevayler.Prevayler;

public abstract class ClockedTransaction extends TransactionWithQuery {
    public Date timeOfExecution;

    public Object executeUsing(Prevayler prevayler) throws Exception {
        return super.executeUsing(prevayler);
    }

    protected final Object executeAndQuery(Object prevalentSystem) throws Exception {
        ClockedSystem clockedSystem = (ClockedSystem) prevalentSystem;
        assert timeOfExecution != null : "Time not generated (transaction not serialized!?)";
        clockedSystem.advanceClockTo(timeOfExecution);
        return executeClocked(clockedSystem);
    }

    protected abstract Object executeClocked(ClockedSystem clockedSystem) throws Exception;

    private void writeObject(ObjectOutputStream out) throws IOException {
        if (timeOfExecution == null) {
            timeOfExecution = new Date();
        }
        out.defaultWriteObject();
    }
}
