package com.tirsen.nanning.samples.prevayler;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;
import java.lang.reflect.AccessibleObject;

import com.tirsen.nanning.Aspects;

public class IdentifyingMarshaller implements Marshaller, Serializable {
    static final long serialVersionUID = 7285806267400465332L;

    public Object marshal(Object o) {
        if (Identity.isPrimitive(o)) {
            return o;

        } else if (Identity.isStatefulService(o.getClass())) {
            assert o instanceof Serializable : "Stateful services must be serializable";
            return o;

        } else if (Identity.isStatelessService(o.getClass())) {
            return new Identity(o.getClass(), Aspects.getAspectInstance(o).getClassIdentifier());

        } else if (Identity.isEntity(o.getClass())) {
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
        if (Identity.isPrimitive(o)) {
            return o;

        } else if (o instanceof Identity) {
            return resolve((Identity) o);

        } else if (Identity.isEntity(o.getClass())) {
            if (!getSystem().hasObjectID(o)) {
                registerObjectIDsRecursive(o);
            }
            return o;

        } else {
            return o;
        }
    }

    private IdentifyingSystem getSystem() {
        Object system = CurrentPrevayler.getSystem();
        assert system instanceof IdentifyingSystem : "I need an identifying system to work";
        return (IdentifyingSystem) system;
    }

    private Object resolve(Identity identity) {
        Class objectClass = identity.getObjectClass();
         if (Identity.isStatelessService(objectClass)) {
            return Aspects.getCurrentAspectFactory().newInstance((Class) identity.getIdentifier());
        }
        if (Identity.isEntity(objectClass)) {
            long oid = ((Long) identity.getIdentifier()).longValue();
            assert getSystem().isIDRegistered(oid) : "object of type " + Aspects.getRealClass(objectClass) + " had invalid object id " + oid;
            return getSystem().getObjectWithID(oid);
        }
        throw new IllegalArgumentException("Can't resolve objects of " + objectClass);
    }

    private void registerObjectIDsRecursive(Object o) {
        final IdentifyingSystem system = getSystem();
        final Set registeredObjects = new HashSet();

        ObjectGraphVisitor.visit(o, new ObjectGraphVisitor() {
            protected void visit(Object o) {
                if (o instanceof AccessibleObject) {
                    return;
                }
                if (o instanceof Date) {
                    return;
                }
                if (Identity.isPrimitive(o)) {
                    return;
                }
                if (!registeredObjects.contains(o) && Identity.isEntity(o.getClass())) {
                    if (system.hasObjectID(o)) {
                        throw new IllegalArgumentException("you're mixing object in prevayler with objects outside, this will lead to unpredictable results, " +
                                                           "so I've banished that sort of behaviour with this assert here, the object that was inside prevayler was " + o);
                    }

                    system.registerObjectID(o);
                    registeredObjects.add(o);
                }
                
                /* for performance, skip the proxy part of all aspected objects */
                if (Aspects.isAspectObject(o)) {
                    Object[] targets = Aspects.getTargets(o);
                    for (int i = 0; i < targets.length; i++) {
                        super.visit(targets[i]);
                    }
                } else {
                    super.visit(o);
                }
            }
        });
    }
}
