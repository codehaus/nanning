package com.tirsen.nanning.samples.rmi;

import java.util.Collection;
import java.util.Iterator;

import com.tirsen.nanning.AspectFactory;
import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.MixinInstance;
import com.tirsen.nanning.attribute.Attributes;
import com.tirsen.nanning.config.Advise;
import com.tirsen.nanning.config.AllPointcut;
import com.tirsen.nanning.config.AspectSystem;
import com.tirsen.nanning.samples.prevayler.Identity;
import com.tirsen.nanning.samples.prevayler.Marshaller;

public class RemoteMarshaller implements Marshaller {
    private ObjectTable objectTable = new ObjectTable();
    private AspectFactory aspectFactory;
    private ServerConnectionManager connectionManager;

    /**
     * Constructs client-side marshaller.
     */
    public RemoteMarshaller() {
        AspectSystem aspectSystem = new AspectSystem();
        aspectFactory = aspectSystem;

        aspectSystem.addPointcut(new AllPointcut(new Advise() {
            public void advise(AspectInstance aspectInstance) {
                MixinInstance mixinInstance = new MixinInstance();
                mixinInstance.setInterfaceClass((Class) aspectInstance.getClassIdentifier());
                aspectInstance.addMixin(mixinInstance);
            }
        }));
        RemoteAspect aspect = new RemoteAspect();
        aspect.setMarshaller(this);
        aspectSystem.addAspect(aspect);
    }

    /**
     * Constructs server-side marshaller.
     */
    public RemoteMarshaller(ServerConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public RemoteMarshaller(AspectFactory aspectFactory) {
        this.aspectFactory = aspectFactory;
    }

    public Object unmarshal(Object o) {
        if (o instanceof Identity) {
            Identity identity = (Identity) o;
            Object id = identity.getIdentifier();
            if (!objectTable.isIDRegistered(id)) {
                // is remote and stub has not been created
                assert !isServerSide() : "did not find server side object in object table " + o;
                Class objectClass = identity.getObjectClass();
                Object stub = aspectFactory.newInstance(objectClass);
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
        return aspectFactory == null;
    }

    public Object marshal(Object o) {
        if (o == null) {
            return null;
        }

        if (Attributes.hasInheritedAttribute(o.getClass(), "remote")) {
            if (isRemoteStub(o)) {
                return getSingleMixinTarget(o);
            } else {
                return new RemoteIdentity((Class) Aspects.getAspectInstance(o).getClassIdentifier(), registerID(o),
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