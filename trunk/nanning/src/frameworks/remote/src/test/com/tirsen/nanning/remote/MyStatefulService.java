package com.tirsen.nanning.remote;

/**
 * @remote
 * @service
 */
public interface MyStatefulService {
    void modify(String value);

    String value();
}
