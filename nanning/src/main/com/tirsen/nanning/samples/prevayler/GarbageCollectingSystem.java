package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.Interceptor;
import org.prevayler.implementation.SnapshotPrevayler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class GarbageCollectingSystem extends SnapshotPrevayler {
    public GarbageCollectingSystem(IdentifyingSystem system, String path) throws IOException, ClassNotFoundException {
        super(system, path);
    }

    public void takeSnapshot() throws IOException {
        IdentifyingSystem system = (IdentifyingSystem) system();
        garbageCollectSystem(system);
        super.takeSnapshot();
    }

    public static void garbageCollectSystem(IdentifyingSystem system) {
        final Set referencedObjects = new HashSet();
        ObjectGraphVisitor.visit(system, new ObjectGraphVisitor() {
            protected void visit(Object o) {
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
                    System.out.println("going in");
                    super.visit(o);
                }
            }
        });
        system.keepTheseObjects(referencedObjects);
    }
}
