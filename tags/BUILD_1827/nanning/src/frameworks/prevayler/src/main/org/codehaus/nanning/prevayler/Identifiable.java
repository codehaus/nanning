package org.codehaus.nanning.prevayler;

/**
 * @entity
 */
public interface Identifiable {

    long getObjectID();

    boolean hasObjectID();

    /**
     * @transaction-required
     */
    void setObjectID(long objectID);

    /**
     * @transaction-required
     */
    void clearObjectID();
}
