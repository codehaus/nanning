package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.ConstructionInvocation;
import org.prevayler.Command;

public interface ConstructCommand extends Command {
    void setInvocation(ConstructionInvocation invocation);
}
