package com.tirsen.nanning.prevayler;

public interface Marshaller {
    Object marshal(Object o);

    Object unmarshal(Object o);
}
