package com.tirsen.nanning.samples.rmi;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import com.tirsen.nanning.samples.prevayler.Marshaller;
import com.tirsen.nanning.samples.prevayler.Identity;
import com.tirsen.nanning.attribute.Attributes;
import com.tirsen.nanning.AspectFactory;
import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.MixinInstance;

public class RemoteMarshaller implements Marshaller {
    private Map objectToId = new HashMap();
    private Map idToObject = new HashMap();
    private long currentId = 0;

    private AspectFactory aspectFactory;

    public RemoteMarshaller() {
    }

    public RemoteMarshaller(AspectFactory aspectFactory) {
        this.aspectFactory = aspectFactory;
    }

    public Object unmarshal(Object o) {
        if (o instanceof Identity) {
            Identity identity = (Identity) o;
            Object id = identity.getIdentifier();
            if (!isIDRegistered(id)) {
                // is remote and stub has not been created
                assert aspectFactory != null : "need aspect-factory to unmarshal remote stub";
                Class objectClass = identity.getObjectClass();
                Object stub = aspectFactory.newInstance(objectClass);
                Aspects.getAspectInstance(stub).setTarget(objectClass, identity);
                return stub;
            } else {
                // is local or stub has already been created
                return getFromID(id);
            }
        }
        return o;
    }

    public Object marshal(Object o) {
        if (o == null) {
            return null;
        }

        if (Attributes.hasInheritedAttribute(o.getClass(), "remote")) {
            if (isRemoteStub(o)) {
                return getSingleMixinTarget(o);
            } else {
                return new Identity((Class) Aspects.getAspectInstance(o).getClassIdentifier(), registerID(o));
            }
        }

        return o;
    }

    public static boolean isRemoteStub(Object o) {
        return getSingleMixinTarget(o) instanceof Identity;
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

    boolean isIDRegistered(Object id) {
        return idToObject.containsKey(id);
    }

    Object getFromID(Object id) {
        Object o = idToObject.get(id);
        assert o != null;
        return o;
    }

    Object registerID(Object o) {
        Object id = objectToId.get(o);
        if (id == null) {
            id = newId();
            objectToId.put(o, id);
            idToObject.put(id, o);
        }
        return id;
    }

    private Object newId() {
        return new Long(currentId++);
    }

    public boolean hasID(Object o) {
        return objectToId.containsKey(o);
    }
}
