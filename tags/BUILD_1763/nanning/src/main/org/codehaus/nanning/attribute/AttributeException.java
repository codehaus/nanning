package org.codehaus.nanning.attribute;

import org.codehaus.nanning.util.WrappedException;

public class AttributeException extends WrappedException {
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
