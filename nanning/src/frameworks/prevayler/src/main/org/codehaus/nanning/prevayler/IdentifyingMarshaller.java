package org.codehaus.nanning.prevayler;

import java.io.Serializable;

import org.codehaus.nanning.Aspects;
import org.codehaus.nanning.AssertionException;

public class IdentifyingMarshaller implements Marshaller, Serializable {
    static final long serialVersionUID = 7285806267400465332L;

    public Object marshal(Object o) {
        if (PrevaylerUtils.isPrimitive(o)) {
            return o;

        } else if (PrevaylerUtils.isStatefulService(o.getClass())) {
            if (!(o instanceof Serializable)) {
                throw new AssertionException("Stateful services must be serializable");
            }
            return o;

        } else if (PrevaylerUtils.isStatelessService(o.getClass())) {
            return new Identity(o.getClass(), Aspects.getAspectInstance(o).getClassIdentifier());

        } else if (getSystem() == o) {
            /* the system itself will get a special ID */
            return new Identity(Aspects.getAspectInstance(o).getClassIdentifier(), new Long(0));

        } else if (PrevaylerUtils.isEntity(o.getClass())) {
            if (getSystem().hasObjectID(o)) {
                return new Identity(Aspects.getAspectInstance(o).getClassIdentifier(), new Long(getSystem().getObjectID(o)));
            } else {
                /* object is not part of target prevalent-system, marshal by value and assign ID at execution */
                return o;
            }

        } else {
            return o;
        }
    }

    public Object unmarshal(Object o) {
        if (PrevaylerUtils.isPrimitive(o)) {
            return o;

        } else if (o instanceof Identity) {
            return resolve((Identity) o);

        } else if (PrevaylerUtils.isEntity(o.getClass())) {
            if (!getSystem().hasObjectID(o)) {
                getSystem().registerObjectID(o);
            }
            return o;

        } else {
            return o;
        }
    }

    private IdentifyingSystem getSystem() {
        Object system = CurrentPrevayler.getSystem();
        return (IdentifyingSystem) system;
    }

    private Object resolve(Identity identity) {
        Class objectClass = identity.getObjectClass();
        if (PrevaylerUtils.isStatelessService(objectClass)) {
            return Aspects.getCurrentAspectFactory().newInstance((Class) identity.getIdentifier());
        }
        if (PrevaylerUtils.isEntity(objectClass)) {
            long oid = ((Long) identity.getIdentifier()).longValue();
            if (!getSystem().isIDRegistered(oid)) {
                throw new AssertionException("object of type " + Aspects.getRealClass(objectClass) + " had invalid object id " + oid);
            }
            return getSystem().getObjectWithID(oid);
        }
        throw new IllegalArgumentException("Can't resolve objects of " + objectClass);
    }
}
