package com.tirsen.nanning.locking;

public interface Lockable {
    void lock();

    boolean isLocked();
}
