package com.tirsen.nanning.samples.rmi;

import java.io.Serializable;

public class ExceptionThrown implements Serializable {
    static final long serialVersionUID = -8737005344833308432L;

    private Throwable throwable;

    public ExceptionThrown(Throwable e) {
        this.throwable = e;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
