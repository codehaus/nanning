package com.tirsen.nanning.samples.prevayler;

import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.Interceptor;
import com.tirsen.nanning.attribute.Attributes;
import org.prevayler.implementation.SnapshotPrevayler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.Collection;
import java.lang.ref.Reference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class GarbageCollectingPrevayler extends SnapshotPrevayler {
    private static final Log logger = LogFactory.getLog(GarbageCollectingPrevayler.class);

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

            if (unreferencedObject instanceof FinalizationCallback) {
                logger.debug("Finalizing object: " + unreferencedObject);
                FinalizationCallback finalizationCallback = (FinalizationCallback) unreferencedObject;
                finalizationCallback.finalizationCallback();
            }

            logger.debug("Unregistering object: " + unreferencedObject);
            system.unregisterObjectID(unreferencedObject);
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
                    logger.debug("Adding referenced object: " + o);
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
                } else {
                    super.visit(o);
                }
            }
        });
        return referencedObjects;
    }
}
