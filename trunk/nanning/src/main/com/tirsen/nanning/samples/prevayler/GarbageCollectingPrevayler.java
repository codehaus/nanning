package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.Interceptor;
import org.prevayler.implementation.SnapshotPrevayler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.Collection;
import java.lang.ref.Reference;

public class GarbageCollectingPrevayler extends SnapshotPrevayler {
    public GarbageCollectingPrevayler(IdentifyingSystem system, String path) throws IOException, ClassNotFoundException {
        super(system, path);
    }

    public void takeSnapshot() throws IOException {
        IdentifyingSystem system = (IdentifyingSystem) system();
        garbageCollectSystem(system);
        super.takeSnapshot();
    }

    public static void garbageCollectSystem(IdentifyingSystem system) {
        Set referencedObjects = getReferencedObjects(system);
        Collection unreferencedObjects = new HashSet(system.getAllRegisteredObjects());
        unreferencedObjects.removeAll(referencedObjects);

        for (Iterator iterator = unreferencedObjects.iterator(); iterator.hasNext();) {
            Object unreferencedObject = iterator.next();
            system.unregisterObjectID(unreferencedObject);
            if (unreferencedObject instanceof FinalizationCallback) {
                FinalizationCallback finalizationCallback = (FinalizationCallback) unreferencedObject;
                finalizationCallback.finalizationCallback();
            }
        }
    }

    private static Set getReferencedObjects(IdentifyingSystem system) {
        final Set referencedObjects = new HashSet();
        ObjectGraphVisitor.visit(system, new ObjectGraphVisitor() {
            protected void visit(Object o) {
                if (o instanceof Reference) {
                    return;
                }
                if (Identity.isEntity(o.getClass())) {
                    referencedObjects.add(o);
                }
                if (Aspects.isAspectObject(o)) {
                    // for performance, skip the proxy part of all aspected objects
                    Object[] targets = Aspects.getTargets(o);
                    for (int i = 0; i < targets.length; i++) {
                        super.visit(targets[i]);
                    }
                    Interceptor[] interceptors = Aspects.getInterceptors(o);
                    for (int i = 0; i < interceptors.length; i++) {
                        super.visit(interceptors[i]);
                    }
                } else if (Identity.isMarshalByValue(o)) {
                    // also skip any value objects, they shouldn't contain references to entities anyway
                } else {
                    super.visit(o);
                }
            }
        });
        return referencedObjects;
    }
}
