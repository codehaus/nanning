package com.tirsen.nanning.samples.rmi;

import java.io.Serializable;

public class ExceptionThrown implements Serializable {
    private Throwable throwable;

    public ExceptionThrown(Throwable e) {
        this.throwable = e;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
