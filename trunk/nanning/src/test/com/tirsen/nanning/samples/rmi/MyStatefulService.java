package com.tirsen.nanning.samples.rmi;

/**
 * @remote
 */
public interface MyStatefulService {
    void modify(String value);

    String value();
}
