package com.tirsen.nanning.samples.prevayler;

import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.attribute.Attributes;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.prevayler.Command;
import org.prevayler.implementation.SnapshotPrevayler;

public class GarbageCollectingPrevayler extends SnapshotPrevayler {
    private static final Log logger = LogFactory.getLog(GarbageCollectingPrevayler.class);

    private Object snapshotLock = new Object();

    public GarbageCollectingPrevayler(IdentifyingSystem system, String path) throws IOException, ClassNotFoundException {
        super(system, path);
    }

    public void takeSnapshot() throws IOException {
        synchronized (snapshotLock) {
            logger.info("taking snapshot");
            IdentifyingSystem system = (IdentifyingSystem) system();
            garbageCollectSystem(system);
            super.takeSnapshot();
            logger.info("snapshot taken");
        }
    }

    public Serializable executeCommand(Command command) throws Exception {
        synchronized (snapshotLock) {
            return super.executeCommand(command);
        }
    }

    public static void garbageCollectSystem(IdentifyingSystem system) {
        logger.info("garbage collecting system");
        Set referencedObjects = getReferencedObjects(system);
        Collection unreferencedObjects = new HashSet(system.getAllRegisteredObjects());
        unreferencedObjects.removeAll(referencedObjects);

        logger.info("garbage collect reclaimed " + unreferencedObjects.size() + " number of objects");

        for (Iterator iterator = unreferencedObjects.iterator(); iterator.hasNext();) {
            Object unreferencedObject = iterator.next();

            if (unreferencedObject instanceof FinalizationCallback) {
                FinalizationCallback finalizationCallback = (FinalizationCallback) unreferencedObject;
                finalizationCallback.finalizationCallback();
            }

            if (system.hasObjectID(unreferencedObject)) {
                system.unregisterObjectID(unreferencedObject);
            }
        }
    }

    private static Set getReferencedObjects(IdentifyingSystem system) {
        final Set referencedObjects = new HashSet();
        ObjectGraphVisitor.visit(system, new ObjectGraphVisitor() {
            protected void visitField(Field field, Object container) {
                if (!Modifier.isStatic(field.getModifiers()) && !Attributes.hasInheritedAttribute(field, "weak")) {
                    super.visitField(field, container);
                }
            }

            protected void visit(Object o) {

                if (o instanceof String) {
                    return;
                }

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
                } else {
                    super.visit(o);
                }
            }
        });
        return referencedObjects;
    }
}
