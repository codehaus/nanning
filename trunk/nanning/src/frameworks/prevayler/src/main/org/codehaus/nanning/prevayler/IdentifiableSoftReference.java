package org.codehaus.nanning.prevayler;

import java.lang.ref.SoftReference;
import java.lang.ref.ReferenceQueue;

public class IdentifiableSoftReference extends SoftReference {
    private long id;

    public IdentifiableSoftReference(Identifiable referent, ReferenceQueue q) {
        super(referent, q);
        this.id = referent.getObjectID();
    }

    public long getObjectId() {
        return id;
    }
}
