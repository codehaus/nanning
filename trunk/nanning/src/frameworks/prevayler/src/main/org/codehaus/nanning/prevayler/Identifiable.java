package org.codehaus.nanning.prevayler;

/**
 * @entity
 */
public interface Identifiable {

    long getObjectID();

    boolean hasObjectID();

    /**
     * @transaction
     */
    void setObjectID(long objectID);
}
