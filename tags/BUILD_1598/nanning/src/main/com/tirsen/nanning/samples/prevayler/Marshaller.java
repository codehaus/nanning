package com.tirsen.nanning.samples.prevayler;

public interface Marshaller {
    Object marshal(Object o);

    Object unmarshal(Object o);
}
