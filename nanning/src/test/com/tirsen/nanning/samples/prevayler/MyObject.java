package com.tirsen.nanning.samples.prevayler;

import java.io.Serializable;

/**
 * @instantiation-is-prevayler-command
 */
public interface MyObject extends Serializable {
    String getAttribute();

    /**
     * @prevayler-command
     */
    void setAttribute(String attribute);
}
