package com.tirsen.nanning.samples.rmi;

public class RMIException extends RuntimeException {
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
