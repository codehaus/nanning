package org.codehaus.nanning.attribute;

public class AttributeException extends RuntimeException {
    public AttributeException(String message) {
        super(message);
    }

///CLOVER:OFF
    public AttributeException() {
    }

    public AttributeException(String message, Throwable cause) {
        super(message, cause);
    }

    public AttributeException(Throwable cause) {
        super(cause);
    }
///CLOVER:ON
}
