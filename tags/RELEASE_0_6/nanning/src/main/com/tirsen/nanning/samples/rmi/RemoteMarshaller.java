package com.tirsen.nanning.samples.rmi;

import java.util.Collection;
import java.util.Iterator;

import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.MixinInstance;
import com.tirsen.nanning.attribute.Attributes;
import com.tirsen.nanning.config.Aspect;
import com.tirsen.nanning.config.AspectSystem;
import com.tirsen.nanning.samples.prevayler.Identity;
import com.tirsen.nanning.samples.prevayler.Marshaller;

public class RemoteMarshaller implements Marshaller {
    private ObjectTable objectTable = new ObjectTable();
    private AspectSystem aspectSystem;
    private ServerConnectionManager connectionManager;

    public static RemoteMarshaller createClientSideMarshaller() {
        AspectSystem aspectSystem = new AspectSystem();

        aspectSystem.addAspect(new Aspect() {
            public void adviseMixin(AspectInstance aspectInstance, MixinInstance mixin) {
            }

            public void advise(AspectInstance aspectInstance) {
            }

            public void introduce(AspectInstance aspectInstance) {
                MixinInstance mixinInstance = new MixinInstance();
                mixinInstance.setInterfaceClass(aspectInstance.getClassIdentifier());
                aspectInstance.addMixin(mixinInstance);
            }
        });

        RemoteMarshaller remoteMarshaller = new RemoteMarshaller();

        RemoteAspect aspect = new RemoteAspect();
        aspect.setMarshaller(remoteMarshaller);
        aspectSystem.addAspect(aspect);
        remoteMarshaller.aspectSystem = aspectSystem;

        return remoteMarshaller;
    }

    public static RemoteMarshaller createServerSideMarshaller(ServerConnectionManager connectionManager) {
        RemoteMarshaller remoteMarshaller = new RemoteMarshaller();
        remoteMarshaller.connectionManager = connectionManager;
        return remoteMarshaller;
    }


    private RemoteMarshaller() {
    }

    public Object unmarshal(Object o) {
        if (o instanceof Identity) {
            Identity identity = (Identity) o;
            Object id = identity.getIdentifier();
            if (!objectTable.isIDRegistered(id)) {
                if (isServerSide()) {
                    // is remote and stub has not been created
                    throw new RuntimeException("Did not find remote object on the server side (timeout?): " + o);
                }
                Class objectClass = identity.getObjectClass();
                Object stub = aspectSystem.newInstance(objectClass);
                Aspects.getAspectInstance(stub).setTarget(objectClass, identity);
                return stub;
            } else {
                // is local or stub has already been created
                return objectTable.getFromID(id);
            }
        }
        return o;
    }

    private boolean isServerSide() {
        return aspectSystem == null;
    }

    public Object marshal(Object o) {
        if (o == null) {
            return null;
        }

        if (Attributes.hasInheritedAttribute(o.getClass(), "remote")) {
            if (isRemoteStub(o)) {
                return getSingleMixinTarget(o);
            } else {
                return new RemoteIdentity(Aspects.getAspectInstance(o).getClassIdentifier(), registerID(o),
                                          connectionManager);
            }
        } else {
            assert Aspects.isAspectObject(o) ? !isRemoteStub(o) : true
                    : o + " was remote stub but did not have 'remote'-attribute";
        }

        return o;
    }

    Object registerID(Object o) {
        return objectTable.register(o);
    }

    public static boolean isRemoteStub(Object o) {
        return getSingleMixinTarget(o) instanceof RemoteIdentity;
    }

    private static Object getSingleMixinTarget(Object o) {
        Collection mixins = Aspects.getAspectInstance(o).getMixins();
        Iterator iterator = mixins.iterator();
        assert iterator.hasNext() : o + " doesn't have any mixins";
        MixinInstance mixinInstance = (MixinInstance) iterator.next();
        assert !iterator.hasNext() : "don't support several mixins";

        Object target = mixinInstance.getTarget();
        return target;
    }

    public void reset() {
        objectTable.clear();
    }
}