package com.tirsen.nanning.samples.prevayler;

import java.lang.reflect.AccessibleObject;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.tirsen.nanning.Aspects;

public class IdentifyingMarshaller implements Marshaller {
    public Object marshal(Object o) {
        if (Identity.isPrimitive(o)) {
            return o;
        } else if (Identity.isStatefulService(o.getClass())) {
            return o;
        } else if (Identity.isStatelessService(o.getClass())) {
            return new Identity(o.getClass(), Aspects.getAspectInstance(o).getClassIdentifier());
        } else if (Identity.isEntity(o.getClass())) {
            if (CurrentPrevayler.getSystem().hasObjectID(o)) {
                return new Identity(o.getClass(), new Long(CurrentPrevayler.getSystem().getObjectID(o)));
            } else {
                // object is not part of target prevalent-system, marshal by value and assign ID at execution
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
            return ((Identity) o).resolve(Aspects.getCurrentAspectFactory(), CurrentPrevayler.getSystem());
        } else if (Identity.isEntity(o.getClass())) {
            if (!CurrentPrevayler.getSystem().hasObjectID(o)) {
                registerObjectIDsRecursive(o);
            }
            return o;
        } else {
            return o;
        }
    }

    private void registerObjectIDsRecursive(Object o) {
        final IdentifyingSystem system = CurrentPrevayler.getSystem();
        final Set registeredObjects = new HashSet();
        ObjectGraphVisitor.visit(o, new ObjectGraphVisitor() {
            protected void visit(Object o) {
                if (o instanceof AccessibleObject) {
                    return;
                }
                if (o instanceof Class) {
                    return;
                }
                if (o instanceof Date) {
                    return;
                }
                if (Identity.isPrimitive(o)) {
                    return;
                }
                if (!registeredObjects.contains(o) && Identity.isEntity(o.getClass())) {
                    assert !system.hasObjectID(o) :
                            "you're mixing object in prevayler with objects outside, this will lead to unpredictable results, " +
                            "so I've banished that sort of behaviour with this assert here, the object that was inside prevayler was " + o;
                    system.registerObjectID(o);
                    registeredObjects.add(o);
                }
                // for performance, skip the proxy part of all aspected objects
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
