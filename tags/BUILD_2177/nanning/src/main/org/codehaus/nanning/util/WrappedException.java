package org.codehaus.nanning.util;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * This is needed since the RuntimeException in 1.3 cant have a cause exception
 */
public class WrappedException extends RuntimeException {
    private Throwable cause;

    public WrappedException() {
    }

    public WrappedException(String s) {
        super(s);
    }

    public WrappedException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    public WrappedException(Throwable cause) {
        this(null, cause);
    }

    public Throwable getCause() {
        return cause;
    }

    public void printStackTrace() {
        super.printStackTrace();
        if (cause != null) {
            System.err.println("Caused by:");
            cause.printStackTrace();
        }
    }

    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
        if (cause != null) {
            s.println("Caused by:");
            cause.printStackTrace(s);
        }
    }

    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);
        if (cause != null) {
            s.println("Caused by:");
            cause.printStackTrace(s);
        }
    }
}
