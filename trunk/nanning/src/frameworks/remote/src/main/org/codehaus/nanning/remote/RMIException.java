package org.codehaus.nanning.remote;

import org.codehaus.nanning.util.WrappedException;

public class RMIException extends WrappedException {
    public RMIException() {
    }

    public RMIException(String message) {
        super(message);
    }

    public RMIException(String message, Throwable cause) {
        super(message, cause);
    }

    public RMIException(Throwable cause) {
        super(cause);
    }
}
