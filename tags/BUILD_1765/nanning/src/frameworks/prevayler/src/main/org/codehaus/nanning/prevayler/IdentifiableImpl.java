package org.codehaus.nanning.prevayler;

import java.io.Serializable;

import org.codehaus.nanning.Aspects;
import org.codehaus.nanning.AssertionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IdentifiableImpl implements Identifiable, Serializable {
    private static final Log logger = LogFactory.getLog(IdentifiableImpl.class);
    static final long serialVersionUID = 2383678917059372958L;

    private long objectID = -1;

    public long getObjectID() {
        if (!hasObjectID()) {
            throw new AssertionException("object " + Aspects.getThis() + " had no object id, use BasicIdentifyingSystem.register(Object)");
        }
        return objectID;
    }

    public boolean hasObjectID() {
        return objectID != -1;
    }

    public void setObjectID(long objectID) {
        if (hasObjectID()) {
            throw new AssertionException("already has ID: " + Aspects.getThis());
        }
        logger.debug("Assigned id " + objectID + " to object " + Aspects.getThis());
        this.objectID = objectID;
    }
}
