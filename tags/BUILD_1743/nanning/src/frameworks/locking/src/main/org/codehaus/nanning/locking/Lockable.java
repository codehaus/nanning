package org.codehaus.nanning.locking;

public interface Lockable {
    void lock();

    boolean isLocked();
}
