package org.codehaus.nanning.prevayler;

import org.codehaus.nanning.prevayler.Marshaller;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.IOException;

public class MarshallingOutputStream extends ObjectOutputStream {
    private Marshaller marshaller;

    public MarshallingOutputStream(OutputStream out, Marshaller marshaller) throws IOException {
        super(out);
        this.marshaller = marshaller;
        enableReplaceObject(true);
    }

    protected Object replaceObject(Object obj) throws IOException {
        return marshaller.marshal(obj);
    }
}
