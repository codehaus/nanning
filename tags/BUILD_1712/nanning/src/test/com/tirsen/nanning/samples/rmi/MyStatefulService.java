package com.tirsen.nanning.samples.rmi;

/**
 * @remote
 * @service
 */
public interface MyStatefulService {
    void modify(String value);

    String value();
}
