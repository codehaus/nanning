package org.codehaus.nanning.prevayler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.nanning.AssertionException;

public class BasicIdentifyingSystem extends IdentifiableImpl implements IdentifyingSystem, Serializable {
    private static final Log logger = LogFactory.getLog(BasicIdentifyingSystem.class);
    static final long serialVersionUID = 4503034161857395426L;

    private boolean somethingWasGCd;
    private Map idToObject = new HashMap();
    private transient ReferenceQueue queue = new ReferenceQueue();
    private transient List readBackValues;

    private long nextObjectId = 0;

    private void removeUnreferencedKeys() {
        SoftReference reference = (SoftReference) queue.poll();
        if (reference == null) {
            return;
        }

        somethingWasGCd = true;
        idToObject.remove(new Long(((IdentifiableSoftReference) reference).getObjectId()));
    }

    public boolean hasBeenGCdSinceLastCall() {
        periodicalMaintenanceOperation();
        if (somethingWasGCd) {
            somethingWasGCd = false;
            return true;
        }
        return false;
    }

    public Identifiable getIdentifiable(long id) {
        periodicalMaintenanceOperation();
        SoftReference reference = getReference(id);
        if (reference == null || isUpForGC(reference)) {
            return null;
        }

        Identifiable object = (Identifiable) reference.get();
        if (object == null) {
            throw new AssertionException("could not find object with id " + id);
        }
        if (!object.hasObjectID()) {
            throw new AssertionException("object is not registered " + object);
        }

        return object;
    }

    public long register(Object object) {
        periodicalMaintenanceOperation();
        if (!CurrentPrevayler.isInTransaction()) {
            throw new IllegalStateException("You have to be inside a transaction to register objects");
        }

        if (!(object instanceof Identifiable)) {
            throw new AssertionException("Object is not instance of Identifiable");
        }

        long id = getNextId();

        Identifiable identifiable = (Identifiable) object;

        if (identifiable.hasObjectID()) {
            throw new AssertionException("Object already registered");
        }

        identifiable.setObjectID(id);

        if (isIDRegistered(id)) {
            throw new AssertionException("Object already registered");
        }

        register(identifiable);

        return id;
    }

    public boolean isIDRegistered(long id) {
        periodicalMaintenanceOperation();
        SoftReference reference = getReference(id);
        if (reference == null || isUpForGC(reference)) {
            return false;
        }
        return idToObject.containsKey(new Long(id));
    }

    public boolean hasNoRegisteredObjects() {
        periodicalMaintenanceOperation();
        return getAllRegisteredObjects().isEmpty();
    }

    public Collection getAllRegisteredObjects() {
        periodicalMaintenanceOperation();
        List result = new ArrayList();
        for (Iterator i = idToObject.values().iterator(); i.hasNext();) {
            SoftReference reference = (SoftReference) i.next();
            if (!isUpForGC(reference)) {
                result.add(reference.get());
            }
        }
        return result;
    }

    private void register(Identifiable identifiable) {
        logger.debug("registering object " + identifiable + " with id " + identifiable.getObjectID());
        idToObject.put(new Long(identifiable.getObjectID()), new IdentifiableSoftReference(identifiable, queue));
    }

    private boolean isUpForGC(SoftReference reference) {
        return reference.isEnqueued() || reference.get() == null;
    }

    private SoftReference getReference(long id) {
        return (SoftReference) idToObject.get(new Long(id));
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(getAllRegisteredObjects());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.idToObject = new HashMap();
        this.queue = new ReferenceQueue();
        this.readBackValues = new ArrayList();
        Collection registeredObjects = (Collection) in.readObject();
        for (Iterator i = registeredObjects.iterator(); i.hasNext();) {
            Identifiable identifiable = (Identifiable) i.next();
            readBackValues.add(identifiable);
        }
    }

    private void periodicalMaintenanceOperation() {
        reinitValues();
        removeUnreferencedKeys();
    }

    private void reinitValues() {
        if (readBackValues == null) {
            return;
        }
        for (Iterator i = readBackValues.iterator(); i.hasNext();) {
            Identifiable identifiable = (Identifiable) i.next();
            register(identifiable);
        }
        readBackValues = null;
    }

    private synchronized long getNextId() {
        return nextObjectId++;
    }
}