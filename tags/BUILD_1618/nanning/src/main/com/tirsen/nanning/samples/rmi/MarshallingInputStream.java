package com.tirsen.nanning.samples.rmi;

import com.tirsen.nanning.samples.prevayler.Marshaller;

import java.io.ObjectInputStream;
import java.io.InputStream;
import java.io.IOException;

public class MarshallingInputStream extends ObjectInputStream {
    private Marshaller marshaller;

    public MarshallingInputStream(InputStream in, Marshaller marshaller) throws IOException {
        super(in);
        this.marshaller = marshaller;
        enableResolveObject(true);
    }

    protected Object resolveObject(Object obj) throws IOException {
        return marshaller.unmarshal(obj);
    }
}
