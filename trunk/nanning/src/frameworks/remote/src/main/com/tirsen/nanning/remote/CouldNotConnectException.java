package com.tirsen.nanning.remote;

import com.tirsen.nanning.remote.CommunicationException;

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
