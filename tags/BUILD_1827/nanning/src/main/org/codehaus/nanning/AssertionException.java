package org.codehaus.nanning;

public class AssertionException extends RuntimeException {
    public AssertionException(String reason) {
        super(reason);
    }

    public AssertionException() {   
    }
}
