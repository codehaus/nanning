package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.Invocation;
import org.prevayler.Command;

/**
 * TODO document InvocationCommand
 *
 * @author <a href="mailto:jon_tirsen@yahoo.com">Jon Tirsén</a>
 * @version $Revision: 1.1 $
 */
public interface InvocationCommand extends Command {
    public void setInvocation(Invocation invocation);
}
