package com.tirsen.nanning.samples.prevayler;

import java.io.Serializable;

/**
 * @instantiation-is-prevayler-command
 */
public interface MyObject extends Serializable, Identifiable {
    String getAttribute();

    /**
     * @prevayler-command
     */
    void setAttribute(String attribute);
}
