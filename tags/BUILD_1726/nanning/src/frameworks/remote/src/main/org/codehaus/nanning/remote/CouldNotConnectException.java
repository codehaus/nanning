package org.codehaus.nanning.remote;

import org.codehaus.nanning.remote.CommunicationException;

public class CouldNotConnectException extends CommunicationException {
    public CouldNotConnectException() {
    }

    public CouldNotConnectException(String message) {
        super(message);
    }

    public CouldNotConnectException(String message, Throwable cause) {
        super(message, cause);
    }

    public CouldNotConnectException(Throwable cause) {
        super(cause);
    }
}
